package com.wuli.badminton.exception;

import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 * 
 * 捕获系统中所有控制器抛出的异常，并转换为统一的响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVo<?> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return ResponseVo.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVo<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        
        for (ObjectError error : bindingResult.getAllErrors()) {
            errors.add(error.getDefaultMessage());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("errors", errors);
        
        logger.error("参数验证失败: {}", errors);
        return ResponseVo.paramError(data);
    }
    
    /**
     * 处理请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseVo<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("请求体格式错误: {}", ex.getMessage());
        return ResponseVo.error(ResponseEnum.PARAM_ERROR, "请求体格式错误");
    }
    
    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseVo<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("不支持的请求方法: {}", ex.getMessage());
        return ResponseVo.error(ResponseEnum.ERROR, "不支持的请求方法: " + ex.getMethod());
    }
    
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseVo<?> handleAccessDenied(AccessDeniedException ex) {
        logger.error("权限不足: {}", ex.getMessage());
        return ResponseVo.error(ResponseEnum.NEED_LOGIN, "权限不足");
    }
    
    /**
     * 处理文件上传大小超过限制异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseVo<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("文件大小超过限制: {}", e.getMessage());
        return ResponseVo.error(ResponseEnum.FILE_SIZE_EXCEEDED);
    }
    
    /**
     * 处理文件上传异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseVo<?> handleMultipartException(MultipartException e) {
        logger.error("文件上传异常: {}", e.getMessage());
        return ResponseVo.error(ResponseEnum.FILE_UPLOAD_ERROR);
    }
    
    /**
     * 处理IO异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVo<?> handleIOException(IOException e) {
        logger.error("IO异常: {}", e.getMessage());
        return ResponseVo.error(ResponseEnum.FILE_UPLOAD_ERROR);
    }
    
    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseVo<?> handleAllExceptions(Exception ex) {
        logger.error("服务器异常", ex);
        return ResponseVo.error(ResponseEnum.SERVER_ERROR);
    }
} 