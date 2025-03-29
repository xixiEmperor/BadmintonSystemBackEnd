package com.wuli.badminton.service.impl;

import com.wuli.badminton.pojo.User;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        logger.info("查找用户: {}, 结果: {}", username, user != null ? "找到" : "未找到");
        return user;
    }

    @Override
    public void save(User user) {
        logger.info("保存用户: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getAvatar() == null) {
            user.setAvatar("https://via.placeholder.com/150");
        }
        userMapper.insert(user);
        logger.info("用户保存成功");
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("加载用户详情: {}", username);
        User user = findByUsername(username);
        if (user == null) {
            logger.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        logger.info("用户详情加载成功: {}", username);
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
} 