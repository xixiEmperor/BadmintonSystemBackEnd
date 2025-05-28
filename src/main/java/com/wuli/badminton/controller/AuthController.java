package com.wuli.badminton.controller;

import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.pojo.User;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/register")
    public ResponseVo<?> register(@RequestBody User user) {
        logger.info("尝试注册: {}", user.getUsername());
        try {
            // 收集验证错误
            List<String> validationErrors = new ArrayList<>();
            
            // 验证用户名长度
            if (user.getUsername() == null || user.getUsername().length() < 5 || user.getUsername().length() > 10) {
                validationErrors.add("用户名长度必须在5-10位之间");
            }
            
            // 验证密码长度
            if (user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 15) {
                validationErrors.add("密码长度必须在6-15位之间");
            }
            
            // 验证邮箱格式
            if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                validationErrors.add("邮箱格式不正确");
            }
            
            // 如果存在验证错误，返回参数错误响应
            if (!validationErrors.isEmpty()) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("errors", validationErrors);
                return ResponseVo.paramError(errors);
            }
            
            // 检查用户名是否存在
            if (userService.findByUsername(user.getUsername()) != null) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
            }
            
            // 检查邮箱是否已被注册
            User emailUser = userService.findByEmail(user.getEmail());
            if (emailUser != null) {
                logger.warn("邮箱已被注册: {}", user.getEmail());
                return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
            }
            
            user.setRole("ROLE_USER");
            userService.save(user);
            logger.info("注册成功: {}", user.getUsername());
            return ResponseVo.success("注册成功", null);
        } catch (Exception e) {
            logger.error("注册失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
        
    }
    
} 