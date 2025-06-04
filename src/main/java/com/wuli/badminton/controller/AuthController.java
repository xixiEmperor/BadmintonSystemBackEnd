package com.wuli.badminton.controller;

import com.wuli.badminton.dto.RegisterDto;
import com.wuli.badminton.dto.ResetPasswordDto;
import com.wuli.badminton.dto.SendCodeRequestDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.EmailService;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.util.JwtUtil;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseVo<?> login(@RequestBody User user) {
        logger.info("尝试登录: {}", user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            String token = jwtUtil.generateToken(authentication);
            User loginUser = userService.findByUsername(user.getUsername());
            
            // 更新最后登录时间
            userService.updateLastLoginTime(loginUser.getId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", loginUser.getId());
            data.put("username", user.getUsername());
            data.put("avatar", loginUser.getAvatar());
            
            logger.info("登录成功: {}", user.getUsername());
            return ResponseVo.success("登录成功", data);
        } catch (BadCredentialsException e) {
            logger.error("用户名或密码错误: {}", user.getUsername());
            return ResponseVo.error(ResponseEnum.LOGIN_ERROR);
        } catch (AuthenticationException e) {
            logger.error("认证失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseVo.error(ResponseEnum.LOGIN_ERROR);
        } catch (Exception e) {
            logger.error("登录失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send-code")
    public ResponseVo<?> sendVerificationCode(@RequestBody SendCodeRequestDto requestDto) {
        logger.info("发送验证码请求: email={}, type={}", requestDto.getEmail(), requestDto.getType());
        
        // 验证邮箱格式
        if (requestDto.getEmail() == null || !EMAIL_PATTERN.matcher(requestDto.getEmail()).matches()) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "邮箱格式不正确");
        }
        
        // 验证类型
        if (!"register".equals(requestDto.getType()) && !"reset".equals(requestDto.getType())) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "验证码类型错误");
        }
        
        // 如果是注册验证码，检查邮箱是否已被注册
        if ("register".equals(requestDto.getType())) {
            User existUser = userService.findByEmail(requestDto.getEmail());
            if (existUser != null) {
                return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
            }
        }
        
        // 如果是重置密码验证码，检查邮箱是否存在
        if ("reset".equals(requestDto.getType())) {
            User existUser = userService.findByEmail(requestDto.getEmail());
            if (existUser == null) {
                return ResponseVo.error(ResponseEnum.EMAIL_NOT_EXIST, "邮箱不存在");
            }
        }
        
        boolean success = emailService.sendVerificationCode(requestDto.getEmail(), requestDto.getType());
        if (success) {
            return ResponseVo.success("验证码发送成功，请查收邮件");
        } else {
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 用户注册接口（支持验证码）
     */
    @PostMapping("/register")
    public ResponseVo<?> register(@RequestBody RegisterDto requestDto) {
        logger.info("尝试注册: username={}, email={}", requestDto.getUsername(), requestDto.getEmail());
        
        try {
            // 收集验证错误
            List<String> validationErrors = new ArrayList<>();
            
            // 验证用户名长度
            if (requestDto.getUsername() == null || requestDto.getUsername().length() < 5 || requestDto.getUsername().length() > 10) {
                validationErrors.add("用户名长度必须在5-10位之间");
            }
            
            // 验证密码长度
            if (requestDto.getPassword() == null || requestDto.getPassword().length() < 6 || requestDto.getPassword().length() > 15) {
                validationErrors.add("密码长度必须在6-15位之间");
            }
            
            // 验证邮箱格式
            if (requestDto.getEmail() == null || !EMAIL_PATTERN.matcher(requestDto.getEmail()).matches()) {
                validationErrors.add("邮箱格式不正确");
            }
            
            // 验证验证码
            if (requestDto.getVerificationCode() == null || requestDto.getVerificationCode().trim().isEmpty()) {
                validationErrors.add("验证码不能为空");
            }
            
            // 如果存在验证错误，返回参数错误响应
            if (!validationErrors.isEmpty()) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("errors", validationErrors);
                return ResponseVo.paramError(errors);
            }
            
            // 验证邮箱验证码
            if (!emailService.verifyCode(requestDto.getEmail(), requestDto.getVerificationCode(), "register")) {
                return ResponseVo.error(ResponseEnum.VERIFICATION_CODE_ERROR, "验证码错误或已过期");
            }
            
            // 检查用户名是否存在
            if (userService.findByUsername(requestDto.getUsername()) != null) {
                logger.warn("用户名已存在: {}", requestDto.getUsername());
                return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
            }
            
            // 检查邮箱是否已被注册
            User emailUser = userService.findByEmail(requestDto.getEmail());
            if (emailUser != null) {
                logger.warn("邮箱已被注册: {}", requestDto.getEmail());
                return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(requestDto.getUsername());
            user.setPassword(requestDto.getPassword());
            user.setEmail(requestDto.getEmail());
            user.setRole("ROLE_USER");
            
            userService.save(user);
            
            // 清除验证码
            emailService.clearCode(requestDto.getEmail(), "register");
            
            logger.info("注册成功: {}", requestDto.getUsername());
            return ResponseVo.success("注册成功", null);
            
        } catch (Exception e) {
            logger.error("注册失败: {}, 错误: {}", requestDto.getUsername(), e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 重置密码接口
     */
    @PostMapping("/reset-password")
    public ResponseVo<?> resetPassword(@RequestBody ResetPasswordDto requestDto) {
        logger.info("尝试重置密码: email={}", requestDto.getEmail());
        
        try {
            // 验证参数
            if (requestDto.getEmail() == null || !EMAIL_PATTERN.matcher(requestDto.getEmail()).matches()) {
                return ResponseVo.error(ResponseEnum.PARAM_ERROR, "邮箱格式不正确");
            }
            
            if (requestDto.getNewPassword() == null || requestDto.getNewPassword().length() < 6 || requestDto.getNewPassword().length() > 15) {
                return ResponseVo.error(ResponseEnum.PARAM_ERROR, "密码长度必须在6-15位之间");
            }
            
            if (requestDto.getVerificationCode() == null || requestDto.getVerificationCode().trim().isEmpty()) {
                return ResponseVo.error(ResponseEnum.PARAM_ERROR, "验证码不能为空");
            }
            
            // 验证邮箱验证码
            if (!emailService.verifyCode(requestDto.getEmail(), requestDto.getVerificationCode(), "reset")) {
                return ResponseVo.error(ResponseEnum.VERIFICATION_CODE_ERROR, "验证码错误或已过期");
            }
            
            // 检查用户是否存在
            User user = userService.findByEmail(requestDto.getEmail());
            if (user == null) {
                return ResponseVo.error(ResponseEnum.EMAIL_NOT_EXIST, "邮箱不存在");
            }
            
            // 重置密码
            userService.resetPassword(user.getId(), requestDto.getNewPassword());
            
            // 清除验证码
            emailService.clearCode(requestDto.getEmail(), "reset");
            
            logger.info("密码重置成功: email={}", requestDto.getEmail());
            return ResponseVo.success("密码重置成功");
            
        } catch (Exception e) {
            logger.error("重置密码失败: email={}, 错误: {}", requestDto.getEmail(), e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
} 