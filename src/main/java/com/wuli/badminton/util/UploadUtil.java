package com.wuli.badminton.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
public class UploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.domain}")
    private String domain;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 上传单个文件到阿里云OSS
     * 
     * @param file 要上传的文件
     * @return 文件访问URL
     * @throws Exception 上传过程中的异常
     */
    public String upload(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            logger.error("上传文件为空");
            throw new RuntimeException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        
        logger.info("开始上传文件到阿里云OSS: endpoint={}, bucketName={}, fileName={}", endpoint, bucketName, fileName);
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 上传文件
            ossClient.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream()));
            String fileUrl = domain + fileName;
            logger.info("文件上传成功，访问地址: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new Exception("文件上传失败: " + e.getMessage(), e);
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
    }
    
    /**
     * 批量上传多个文件到阿里云OSS，返回主图URL和子图URL列表的JSON字符串
     * 
     * @param mainImage 主图
     * @param subImages 子图列表
     * @return JSON格式的图片URL，包含mainImage和subImages字段
     * @throws Exception 上传过程中的异常
     */
    public String uploadProductImages(MultipartFile mainImage, MultipartFile[] subImages) throws Exception {
        if (mainImage == null || mainImage.isEmpty()) {
            logger.error("主图不能为空");
            throw new RuntimeException("主图不能为空");
        }
        
        Map<String, Object> resultMap = new HashMap<>();
        
        // 上传主图
        String mainImageUrl = upload(mainImage);
        resultMap.put("mainImage", mainImageUrl);
        
        // 上传子图（如果有）
        if (subImages != null && subImages.length > 0) {
            List<String> subImageUrls = new ArrayList<>();
            for (MultipartFile subImage : subImages) {
                if (subImage != null && !subImage.isEmpty()) {
                    try {
                        String subImageUrl = upload(subImage);
                        subImageUrls.add(subImageUrl);
                    } catch (Exception e) {
                        logger.warn("子图上传失败: {}", e.getMessage());
                        // 继续上传其他子图
                    }
                }
            }
            resultMap.put("subImages", subImageUrls);
            
            // 将子图URL列表转为逗号分隔的字符串，方便存储在数据库中
            String subImagesStr = String.join(",", subImageUrls);
            resultMap.put("subImagesStr", subImagesStr);
        } else {
            resultMap.put("subImages", Collections.emptyList());
            resultMap.put("subImagesStr", "");
        }
        
        try {
            String jsonResult = objectMapper.writeValueAsString(resultMap);
            logger.info("商品图片上传完成，返回JSON: {}", jsonResult);
            return jsonResult;
        } catch (JsonProcessingException e) {
            logger.error("转换JSON失败: {}", e.getMessage(), e);
            throw new Exception("生成图片JSON失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量上传多个文件到阿里云OSS
     * 
     * @param files 要上传的文件数组
     * @return 上传成功的文件URL列表
     * @throws Exception 上传过程中的异常
     */
    public List<String> uploadBatch(MultipartFile[] files) throws Exception {
        if (files == null || files.length == 0) {
            logger.error("上传文件列表为空");
            throw new RuntimeException("文件列表不能为空");
        }
        
        List<String> urlList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String fileUrl = upload(file);
                    urlList.add(fileUrl);
                } catch (Exception e) {
                    logger.warn("批量上传中单个文件上传失败: {}", e.getMessage());
                    // 继续上传其他文件
                }
            }
        }
        
        if (urlList.isEmpty()) {
            logger.error("批量上传失败，没有一个文件上传成功");
            throw new Exception("批量上传失败，没有一个文件上传成功");
        }
        
        logger.info("批量上传完成，共上传成功{}个文件", urlList.size());
        return urlList;
    }
    
    /**
     * 批量上传多个文件到阿里云OSS，并返回JSON字符串
     * 
     * @param files 要上传的文件数组
     * @return JSON格式的文件URL列表
     * @throws Exception 上传过程中的异常
     */
    public String uploadBatchAsJson(MultipartFile[] files) throws Exception {
        List<String> urls = uploadBatch(files);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("urls", urls);
        resultMap.put("count", urls.size());
        
        try {
            String jsonResult = objectMapper.writeValueAsString(resultMap);
            logger.info("批量上传完成，返回JSON: {}", jsonResult);
            return jsonResult;
        } catch (JsonProcessingException e) {
            logger.error("转换JSON失败: {}", e.getMessage(), e);
            throw new Exception("生成JSON失败: " + e.getMessage(), e);
        }
    }
}
