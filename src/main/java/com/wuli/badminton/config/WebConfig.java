package com.wuli.badminton.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 阿里云OSS已经取代了本地文件存储，因此不再需要配置资源处理器和检查上传目录
} 