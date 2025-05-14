package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.UserDetailMapper;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.dto.UserProfileUpdateDto;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.pojo.UserDetail;
import com.wuli.badminton.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Autowired
    private UserDetailMapper userDetailMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetail findByUserId(Long userId) {
        logger.debug("查询用户详情，用户ID: {}", userId);
        return userDetailMapper.findByUserId(userId);
    }

    @Override
    public void save(UserDetail userDetail) {
        logger.info("保存用户详情，用户ID: {}", userDetail.getUserId());
        userDetailMapper.insert(userDetail);
    }

    @Override
    public void update(UserDetail userDetail) {
        logger.info("更新用户详情，用户ID: {}, 详情ID: {}", userDetail.getUserId(), userDetail.getId());
        userDetailMapper.update(userDetail);
    }

    @Override
    public UserDetail createDefaultDetail(Long userId) {
        logger.info("创建默认用户详情，用户ID: {}", userId);
        // 获取用户信息以便使用用户名作为默认昵称
        User user = userMapper.findById(userId);
        
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(userId);
        userDetail.setNickname(user != null ? user.getUsername() : null); // 默认使用用户名作为昵称
        userDetail.setPhone(null);
        userDetail.setBio("这个人很懒，什么都没留下。");
        userDetail.setGender("未设置");
        userDetail.setBirthday(null);
        userDetail.setLocation("未设置");
        userDetail.setLastLoginAt(null);
        
        // 保存到数据库
        save(userDetail);
        
        return userDetail;
    }
    
    @Override
    public UserDetail updateUserDetail(Long userId, UserProfileUpdateDto updateDto) {
        logger.info("更新用户详情，用户ID: {}", userId);
        
        // 查找用户详情
        UserDetail userDetail = findByUserId(userId);
        
        // 如果用户详情不存在，创建默认详情
        if (userDetail == null) {
            logger.info("用户详情不存在，创建默认详情，用户ID: {}", userId);
            userDetail = createDefaultDetail(userId);
        }
        
        // 更新字段（只更新非null的字段）
        boolean hasChanges = false;
        
        if (updateDto.getNickname() != null) {
            userDetail.setNickname(updateDto.getNickname());
            hasChanges = true;
        }
        
        if (updateDto.getPhone() != null) {
            userDetail.setPhone(updateDto.getPhone());
            hasChanges = true;
        }
        
        if (updateDto.getBio() != null) {
            userDetail.setBio(updateDto.getBio());
            hasChanges = true;
        }
        
        if (updateDto.getGender() != null) {
            userDetail.setGender(updateDto.getGender());
            hasChanges = true;
        }
        
        if (updateDto.getBirthday() != null) {
            userDetail.setBirthday(updateDto.getBirthday());
            hasChanges = true;
        }
        
        if (updateDto.getLocation() != null) {
            userDetail.setLocation(updateDto.getLocation());
            hasChanges = true;
        }
        
        // 如果有字段被更新，才执行更新操作
        if (hasChanges) {
            // 设置更新时间
            userDetail.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            update(userDetail);
            logger.info("用户详情更新成功，用户ID: {}", userId);
        } else {
            logger.info("没有字段需要更新，用户ID: {}", userId);
        }
        
        return userDetail;
    }
} 