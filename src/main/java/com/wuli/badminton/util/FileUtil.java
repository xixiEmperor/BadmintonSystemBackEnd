package com.wuli.badminton.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    
    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    
    // 最大文件大小 (2MB)
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    
    /**
     * 检查文件类型是否为允许的图片类型
     * @param contentType 文件类型
     * @return 是否允许
     */
    public static boolean isAllowedImageType(String contentType) {
        return ALLOWED_IMAGE_TYPES.contains(contentType);
    }
    
    /**
     * 检查文件大小是否超过限制
     * @param size 文件大小
     * @return 是否超过限制
     */
    public static boolean isFileSizeExceeded(long size) {
        return size > MAX_FILE_SIZE;
    }
    
    /**
     * 生成唯一的文件名
     * @param originalFilename 原始文件名
     * @return 新文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }
    
    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * 保存文件到指定路径
     * @param file 文件
     * @param uploadDir 上传目录
     * @param filename 文件名
     * @return 文件路径
     * @throws IOException IO异常
     */
    public static String saveFile(MultipartFile file, String uploadDir, String filename) throws IOException {
        // 确保上传目录存在
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            logger.info("上传目录不存在，尝试创建: {}", directory.getAbsolutePath());
            boolean created = directory.mkdirs();
            if (!created) {
                logger.error("无法创建上传目录: {}", directory.getAbsolutePath());
                throw new IOException("无法创建上传目录: " + directory.getAbsolutePath());
            }
            logger.info("成功创建上传目录: {}", directory.getAbsolutePath());
        } else {
            logger.info("上传目录已存在: {}", directory.getAbsolutePath());
        }
        
        // 完整文件路径
        File destFile = new File(directory, filename);
        
        // 记录目标路径
        logger.info("文件将保存到: {}", destFile.getAbsolutePath());
        
        try (
            InputStream inputStream = file.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(destFile)
        ) {
            // 使用基本的文件复制操作替代transferTo方法
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            if (destFile.exists() && destFile.length() > 0) {
                logger.info("文件保存成功: {} (大小: {} 字节)", destFile.getAbsolutePath(), destFile.length());
            } else {
                logger.warn("文件保存完成，但未检测到文件或文件大小为0: {}", destFile.getAbsolutePath());
            }
            
            return filename;
        } catch (IOException e) {
            logger.error("文件保存失败: {}, 错误: {}", destFile.getAbsolutePath(), e.getMessage(), e);
            throw new IOException("文件保存失败: " + e.getMessage(), e);
        }
    }
} 