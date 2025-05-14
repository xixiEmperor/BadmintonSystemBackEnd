package com.wuli.badminton.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    /**
     * 配置静态资源处理器
     * 使上传的文件可以通过URL访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以分隔符结尾
        String path = uploadPath;
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        
        // 配置上传文件的访问路径
        // 注意：Windows 路径中的反斜杠需要额外处理
        String location = "file:" + path.replace("\\", "/");
        
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(location);
        
        // 记录配置信息到日志
        logger.info("已配置静态资源访问映射: /uploads/avatars/** -> {}", location);
    }
    
    /**
     * 启动时检查上传目录是否存在，如果不存在则创建
     */
    @Bean
    public CommandLineRunner checkUploadDirectory() {
        return args -> {
            logger.info("==============================================");
            logger.info("正在检查上传目录: {}", uploadPath);
            
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                logger.info("上传目录不存在，尝试创建: {}", directory.getAbsolutePath());
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("成功创建上传目录: {}", directory.getAbsolutePath());
                } else {
                    logger.error("无法创建上传目录: {}", directory.getAbsolutePath());
                    logger.error("请确保应用有权限在此位置创建目录，或手动创建该目录");
                }
            } else {
                if (directory.isDirectory()) {
                    logger.info("上传目录已存在且有效: {}", directory.getAbsolutePath());
                    // 检查是否有写入权限
                    if (directory.canWrite()) {
                        logger.info("上传目录可写入");
                        
                        // 尝试写入测试文件以确认权限
                        File testFile = new File(directory, "test.txt");
                        try {
                            if (testFile.createNewFile()) {
                                logger.info("测试文件创建成功，确认目录可写入");
                                testFile.delete();
                            } else {
                                logger.warn("测试文件创建失败，目录可能不可写入");
                            }
                        } catch (Exception e) {
                            logger.error("测试目录写入权限时出错: {}", e.getMessage());
                        }
                    } else {
                        logger.warn("上传目录不可写入，请检查权限: {}", directory.getAbsolutePath());
                    }
                } else {
                    logger.error("上传路径存在但不是一个目录: {}", directory.getAbsolutePath());
                }
            }
            logger.info("==============================================");
        };
    }
} 