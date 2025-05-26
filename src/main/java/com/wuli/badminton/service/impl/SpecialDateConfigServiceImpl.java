package com.wuli.badminton.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dao.SpecialDateConfigMapper;
import com.wuli.badminton.dto.SpecialDateConfigDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.pojo.SpecialDateConfig;
import com.wuli.badminton.service.SpecialDateConfigService;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 特殊日期配置服务实现类
 */
@Service
@Slf4j
public class SpecialDateConfigServiceImpl implements SpecialDateConfigService {
    
    @Autowired
    private SpecialDateConfigMapper specialDateConfigMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public ResponseVo<String> createSpecialDateConfig(SpecialDateConfigDto configDto) {
        log.info("【创建特殊日期配置】配置: {}", configDto);
        
        // 转换DTO为实体
        SpecialDateConfig config = new SpecialDateConfig();
        BeanUtils.copyProperties(configDto, config);
        
        // 解析日期
        Date specialDate = parseDate(configDto.getSpecialDate());
        if (specialDate == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "特殊日期格式错误");
        }
        config.setSpecialDate(specialDate);
        
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        
        int result = specialDateConfigMapper.insert(config);
        
        if (result > 0) {
            log.info("【创建特殊日期配置】创建成功，ID: {}", config.getId());
            return ResponseVo.success("特殊日期配置创建成功");
        } else {
            log.error("【创建特殊日期配置】创建失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "特殊日期配置创建失败");
        }
    }
    
    @Override
    public ResponseVo<PageInfo<SpecialDateConfig>> getAllConfigs(Integer pageNum, Integer pageSize) {
        log.info("【获取特殊日期配置列表】页码: {}, 页大小: {}", pageNum, pageSize);
        
        PageHelper.startPage(pageNum, pageSize);
        List<SpecialDateConfig> configs = specialDateConfigMapper.selectAll();
        PageInfo<SpecialDateConfig> pageInfo = new PageInfo<>(configs);
        
        log.info("【获取特殊日期配置列表】查询成功，共{}条记录", pageInfo.getTotal());
        return ResponseVo.success(pageInfo);
    }
    
    @Override
    public ResponseVo<SpecialDateConfig> getConfigById(Long id) {
        log.info("【获取特殊日期配置详情】ID: {}", id);
        
        SpecialDateConfig config = specialDateConfigMapper.selectById(id);
        if (config == null) {
            log.warn("【获取特殊日期配置详情】配置不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.SPECIAL_DATE_CONFIG_NOT_EXIST);
        }
        
        log.info("【获取特殊日期配置详情】查询成功，配置名称: {}", config.getConfigName());
        return ResponseVo.success(config);
    }
    
    @Override
    public ResponseVo<String> updateSpecialDateConfig(Long id, SpecialDateConfigDto configDto) {
        log.info("【更新特殊日期配置】ID: {}, 配置: {}", id, configDto);
        
        SpecialDateConfig existingConfig = specialDateConfigMapper.selectById(id);
        if (existingConfig == null) {
            log.warn("【更新特殊日期配置】配置不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.SPECIAL_DATE_CONFIG_NOT_EXIST);
        }
        
        // 更新配置
        BeanUtils.copyProperties(configDto, existingConfig);
        existingConfig.setId(id);
        
        // 解析日期
        Date specialDate = parseDate(configDto.getSpecialDate());
        if (specialDate == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "特殊日期格式错误");
        }
        existingConfig.setSpecialDate(specialDate);
        existingConfig.setUpdateTime(new Date());
        
        int result = specialDateConfigMapper.updateById(existingConfig);
        
        if (result > 0) {
            log.info("【更新特殊日期配置】更新成功");
            return ResponseVo.success("特殊日期配置更新成功");
        } else {
            log.error("【更新特殊日期配置】更新失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "特殊日期配置更新失败");
        }
    }
    
    @Override
    public ResponseVo<String> deleteSpecialDateConfig(Long id) {
        log.info("【删除特殊日期配置】ID: {}", id);
        
        SpecialDateConfig config = specialDateConfigMapper.selectById(id);
        if (config == null) {
            log.warn("【删除特殊日期配置】配置不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.SPECIAL_DATE_CONFIG_NOT_EXIST);
        }
        
        int result = specialDateConfigMapper.deleteById(id);
        
        if (result > 0) {
            log.info("【删除特殊日期配置】删除成功，配置名称: {}", config.getConfigName());
            return ResponseVo.success("特殊日期配置删除成功");
        } else {
            log.error("【删除特殊日期配置】删除失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "特殊日期配置删除失败");
        }
    }
    
    @Override
    public ResponseVo<String> toggleConfigStatus(Long id, Integer enabled) {
        log.info("【切换特殊日期配置状态】ID: {}, 启用状态: {}", id, enabled);
        
        SpecialDateConfig config = specialDateConfigMapper.selectById(id);
        if (config == null) {
            log.warn("【切换特殊日期配置状态】配置不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.SPECIAL_DATE_CONFIG_NOT_EXIST);
        }
        
        int result = specialDateConfigMapper.updateEnabledStatus(id, enabled);
        
        if (result > 0) {
            String statusDesc = enabled == 1 ? "启用" : "禁用";
            log.info("【切换特殊日期配置状态】{}成功", statusDesc);
            return ResponseVo.success("配置" + statusDesc + "成功");
        } else {
            log.error("【切换特殊日期配置状态】操作失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "状态切换失败");
        }
    }
    
    @Override
    public List<SpecialDateConfig> getConfigsByDate(String date) {
        log.info("【根据日期获取特殊配置】日期: {}", date);
        
        Date specialDate = parseDate(date);
        if (specialDate == null) {
            log.warn("【根据日期获取特殊配置】日期格式错误: {}", date);
            return null;
        }
        
        List<SpecialDateConfig> configs = specialDateConfigMapper.selectByDate(specialDate);
        log.info("【根据日期获取特殊配置】找到{}条配置", configs.size());
        
        return configs;
    }
    
    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.error("日期解析失败: {}", dateStr, e);
            return null;
        }
    }
} 