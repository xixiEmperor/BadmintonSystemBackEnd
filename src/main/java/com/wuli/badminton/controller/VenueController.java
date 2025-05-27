package com.wuli.badminton.controller;

import com.wuli.badminton.dto.VenueDto;
import com.wuli.badminton.dto.VenueStatusMatrixDto;
import com.wuli.badminton.dto.VenueAvailabilityQueryDto;
import com.wuli.badminton.service.VenueService;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueVo;
import com.wuli.badminton.vo.VenueStatusMatrixVo;
import com.wuli.badminton.vo.VenueAvailabilityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 场地Controller
 */
@RestController
@RequestMapping("/api/venue")
@Slf4j
public class VenueController {
    
    @Autowired
    private VenueService venueService;
    
    /**
     * 获取所有场地列表
     */
    @GetMapping("/list")
    public ResponseVo<List<VenueVo>> getAllVenues() {
        return venueService.getAllVenues();
    }
    
    /**
     * 获取场地状态矩阵（用于可视化展示）
     */
    @GetMapping("/status-matrix")
    public ResponseVo<VenueStatusMatrixVo> getVenueStatusMatrix(@Valid VenueStatusMatrixDto dto) {
        log.info("【场地状态矩阵】查询日期: {}, 场地ID: {}", dto.getDate(), dto.getVenueId());
        return venueService.getVenueStatusMatrix(dto);
    }
    
    /**
     * 按时间段查询场地可用性（用于查找可用场地）
     */
    @GetMapping("/availability")
    public ResponseVo<VenueAvailabilityVo> queryVenueAvailability(@Valid VenueAvailabilityQueryDto dto) {
        log.info("【查询场地可用性】日期: {}, 时间段: {}-{}", dto.getDate(), dto.getStartTime(), dto.getEndTime());
        return venueService.queryVenueAvailability(dto);
    }
    
    /**
     * 根据状态获取场地列表
     */
    @GetMapping("/list/status/{status}")
    public ResponseVo<List<VenueVo>> getVenuesByStatus(@PathVariable Integer status) {
        return venueService.getVenuesByStatus(status);
    }
    
    /**
     * 根据ID获取场地详情
     */
    @GetMapping("/{id}")
    public ResponseVo<VenueVo> getVenueById(@PathVariable Integer id) {
        return venueService.getVenueById(id);
    }
    
    /**
     * 新增场地（管理员权限）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> addVenue(@Valid @RequestBody VenueDto venueDto) {
        log.info("【场地管理】管理员新增场地，场地名称: {}", venueDto.getName());
        return venueService.addVenue(venueDto);
    }
    
    /**
     * 更新场地信息（管理员权限）
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> updateVenue(@PathVariable Integer id, @Valid @RequestBody VenueDto venueDto) {
        log.info("【场地管理】管理员更新场地信息，场地ID: {}", id);
        return venueService.updateVenue(id, venueDto);
    }
    
    /**
     * 更新场地状态（管理员权限）
     * 场地基础状态：0-未启用，1-启用
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> updateVenueStatus(@PathVariable Integer id, @RequestParam Integer status) {
        log.info("【场地管理】管理员更新场地状态，场地ID: {}, 状态: {}", id, status);
        return venueService.updateVenueStatus(id, status);
    }
    
    /**
     * 删除场地（管理员权限）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> deleteVenue(@PathVariable Integer id) {
        log.info("【场地管理】管理员删除场地，场地ID: {}", id);
        return venueService.deleteVenue(id);
    }
} 