package com.wuli.badminton.service.impl;

import com.wuli.badminton.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 邮件服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    // 发送间隔限制（秒）
    private static final int SEND_INTERVAL_SECONDS = 60;
    
    @Override
    public boolean sendVerificationCode(String email, String type) {
        try {
            // 检查发送频率限制
            String sendKey = "email_send_limit:" + email + ":" + type;
            if (redisTemplate.hasKey(sendKey)) {
                logger.warn("发送验证码太频繁: email={}, type={}", email, type);
                return false;
            }
            
            // 生成6位随机数字验证码
            String code = generateCode();
            
            // 构建邮件内容
            String subject = getEmailSubject(type);
            String content = getEmailContent(code, type);
            
            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("cdengz@126.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            
            // 保存验证码到Redis
            String codeKey = "email_code:" + email + ":" + type;
            redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 设置发送频率限制
            redisTemplate.opsForValue().set(sendKey, "1", SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);
            
            logger.info("验证码发送成功: email={}, type={}, code={}", email, type, code);
            return true;
            
        } catch (Exception e) {
            logger.error("发送验证码失败: email={}, type={}, error={}", email, type, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String email, String code, String type) {
        try {
            String codeKey = "email_code:" + email + ":" + type;
            String storedCode = redisTemplate.opsForValue().get(codeKey);
            
            if (storedCode == null) {
                logger.warn("验证码不存在或已过期: email={}, type={}", email, type);
                return false;
            }
            
            if (!storedCode.equals(code)) {
                logger.warn("验证码错误: email={}, type={}, input={}, stored={}", email, type, code, storedCode);
                return false;
            }
            
            logger.info("验证码验证成功: email={}, type={}", email, type);
            return true;
            
        } catch (Exception e) {
            logger.error("验证验证码失败: email={}, type={}, error={}", email, type, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void clearCode(String email, String type) {
        try {
            String codeKey = "email_code:" + email + ":" + type;
            redisTemplate.delete(codeKey);
            logger.info("验证码已清除: email={}, type={}", email, type);
        } catch (Exception e) {
            logger.error("清除验证码失败: email={}, type={}, error={}", email, type, e.getMessage(), e);
        }
    }
    
    /**
     * 生成6位随机数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 获取邮件主题
     */
    private String getEmailSubject(String type) {
        switch (type) {
            case "register":
                return "【羽毛球系统】注册验证码";
            case "reset":
                return "【羽毛球系统】密码重置验证码";
            default:
                return "【羽毛球系统】验证码";
        }
    }
    
    /**
     * 获取邮件内容
     */
    private String getEmailContent(String code, String type) {
        String purpose = "";
        switch (type) {
            case "register":
                purpose = "注册账号";
                break;
            case "reset":
                purpose = "重置密码";
                break;
            default:
                purpose = "验证身份";
        }
        
        return String.format(
            "您好！\n\n" +
            "您正在进行%s操作，验证码为：%s\n\n" +
            "验证码有效期为%d分钟，请及时使用。\n" +
            "如果这不是您的操作，请忽略此邮件。\n\n" +
            "-----\n" +
            "武汉理工大学羽毛球系统",
            purpose, code, CODE_EXPIRE_MINUTES
        );
    }
} 