package com.wuli.badminton.controller;

import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("尝试登录: {}", user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            String token = jwtUtil.generateToken(authentication);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            logger.info("登录成功: {}", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.error("用户名或密码错误: {}", user.getUsername());
            return ResponseEntity.badRequest().body("{\"message\":\"用户名或密码错误\"}");
        } catch (AuthenticationException e) {
            logger.error("认证失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"认证失败\"}");
        } catch (Exception e) {
            logger.error("登录失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"登录失败\"}");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("尝试注册: {}", user.getUsername());
        try {
            if (userService.findByUsername(user.getUsername()) != null) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return ResponseEntity.badRequest().body("{\"message\":\"用户名已存在\"}");
            }
            
            user.setRole("ROLE_USER");
            userService.save(user);
            logger.info("注册成功: {}", user.getUsername());
            return ResponseEntity.ok("{\"message\":\"注册成功\"}");
        } catch (Exception e) {
            logger.error("注册失败: {}, 错误: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"注册失败\"}");
        }
    }
} 