package com.wuli.badminton.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis JSON类型处理器
 * 用于将Java对象与数据库JSON类型之间进行转换
 * @param <T> 要转换的Java类型
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;
    
    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            logger.error("Error converting Java object to JSON: {}", e.getMessage(), e);
            throw new SQLException("Error converting Java object to JSON", e);
        }
    }
    
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }
    
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }
    
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }
    
    private T parseJson(String json) {
        if (json == null) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Error parsing JSON to Java object: {}", e.getMessage(), e);
            return null;
        }
    }
} 