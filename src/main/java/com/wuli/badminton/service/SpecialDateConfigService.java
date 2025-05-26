package com.wuli.badminton.service;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dto.SpecialDateConfigDto;
import com.wuli.badminton.pojo.SpecialDateConfig;
import com.wuli.badminton.vo.ResponseVo;
import java.util.List;

/**
 * 特殊日期配置服务接口
 */
public interface SpecialDateConfigService {
    
    /**
     * 创建特殊日期配置
     */
    ResponseVo<String> createSpecialDateConfig(SpecialDateConfigDto configDto);
    
    /**
     * 获取所有特殊日期配置
     */
    ResponseVo<PageInfo<SpecialDateConfig>> getAllConfigs(Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID获取特殊日期配置
     */
    ResponseVo<SpecialDateConfig> getConfigById(Long id);
    
    /**
     * 更新特殊日期配置
     */
    ResponseVo<String> updateSpecialDateConfig(Long id, SpecialDateConfigDto configDto);
    
    /**
     * 删除特殊日期配置
     */
    ResponseVo<String> deleteSpecialDateConfig(Long id);
    
    /**
     * 启用/禁用特殊日期配置
     */
    ResponseVo<String> toggleConfigStatus(Long id, Integer enabled);
    
    /**
     * 根据日期获取特殊配置
     */
    List<SpecialDateConfig> getConfigsByDate(String date);
} 