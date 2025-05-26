package com.wuli.badminton.controller;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dto.SpecialDateConfigDto;
import com.wuli.badminton.pojo.SpecialDateConfig;
import com.wuli.badminton.service.SpecialDateConfigService;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 特殊日期配置控制器
 */
@RestController
@RequestMapping("/api/venue/special-config")
@Slf4j
public class SpecialDateConfigController {
    
    @Autowired
    private SpecialDateConfigService specialDateConfigService;
    
    /**
     * 创建特殊日期配置（管理员权限）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> createSpecialDateConfig(@Valid @RequestBody SpecialDateConfigDto configDto) {
        log.info("【创建特殊日期配置】请求参数: {}", configDto);
        return specialDateConfigService.createSpecialDateConfig(configDto);
    }
    
    /**
     * 获取所有特殊日期配置（管理员权限）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<PageInfo<SpecialDateConfig>> getAllConfigs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("【获取特殊日期配置列表】页码: {}, 页大小: {}", pageNum, pageSize);
        return specialDateConfigService.getAllConfigs(pageNum, pageSize);
    }
    
    /**
     * 根据ID获取特殊日期配置详情（管理员权限）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<SpecialDateConfig> getConfigById(@PathVariable Long id) {
        log.info("【获取特殊日期配置详情】ID: {}", id);
        return specialDateConfigService.getConfigById(id);
    }
    
    /**
     * 更新特殊日期配置（管理员权限）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> updateSpecialDateConfig(
            @PathVariable Long id,
            @Valid @RequestBody SpecialDateConfigDto configDto) {
        log.info("【更新特殊日期配置】ID: {}, 请求参数: {}", id, configDto);
        return specialDateConfigService.updateSpecialDateConfig(id, configDto);
    }
    
    /**
     * 删除特殊日期配置（管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> deleteSpecialDateConfig(@PathVariable Long id) {
        log.info("【删除特殊日期配置】ID: {}", id);
        return specialDateConfigService.deleteSpecialDateConfig(id);
    }
    
    /**
     * 启用/禁用特殊日期配置（管理员权限）
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> toggleConfigStatus(
            @PathVariable Long id,
            @RequestParam Integer enabled) {
        log.info("【切换特殊日期配置状态】ID: {}, 启用状态: {}", id, enabled);
        return specialDateConfigService.toggleConfigStatus(id, enabled);
    }
} 