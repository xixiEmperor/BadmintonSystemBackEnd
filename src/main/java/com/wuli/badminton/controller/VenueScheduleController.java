package com.wuli.badminton.controller;

import com.wuli.badminton.dto.VenueScheduleQueryDto;
import com.wuli.badminton.service.VenueScheduleService;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueScheduleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 场地时间表控制器
 */
@RestController
@RequestMapping("/api/venue/schedule")
@Slf4j
public class VenueScheduleController {
    
    @Autowired
    private VenueScheduleService venueScheduleService;
    
    /**
     * 获取场地预约情况矩阵图
     */
    @GetMapping("/matrix")
    public ResponseVo<Map<String, Object>> getVenueScheduleMatrix(
            @RequestParam String scheduleDate,
            @RequestParam(required = false) Integer venueId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer bookable) {
        log.info("【获取场地预约矩阵】日期: {}, 场地ID: {}", scheduleDate, venueId);
        
        // 构建查询DTO
        VenueScheduleQueryDto queryDto = new VenueScheduleQueryDto();
        queryDto.setScheduleDate(scheduleDate);
        queryDto.setVenueId(venueId);
        queryDto.setStartTime(startTime);
        queryDto.setEndTime(endTime);
        queryDto.setStatus(status);
        queryDto.setBookable(bookable);
        
        return venueScheduleService.getVenueScheduleMatrix(queryDto);
    }
    
    /**
     * 根据条件查询场地时间表
     */
    @GetMapping("/list")
    public ResponseVo<List<VenueScheduleVo>> getVenueSchedules(
            @RequestParam String scheduleDate,
            @RequestParam(required = false) Integer venueId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer bookable) {
        log.info("【查询场地时间表】日期: {}, 场地ID: {}", scheduleDate, venueId);
        
        // 构建查询DTO
        VenueScheduleQueryDto queryDto = new VenueScheduleQueryDto();
        queryDto.setScheduleDate(scheduleDate);
        queryDto.setVenueId(venueId);
        queryDto.setStartTime(startTime);
        queryDto.setEndTime(endTime);
        queryDto.setStatus(status);
        queryDto.setBookable(bookable);
        
        return venueScheduleService.getVenueSchedules(queryDto);
    }
    
    /**
     * 生成指定日期的场地时间表（管理员权限）
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> generateScheduleForDate(@RequestParam String date) {
        log.info("【生成场地时间表】日期: {}", date);
        return venueScheduleService.generateScheduleForDate(date);
    }
    
    /**
     * 批量生成未来几天的场地时间表（管理员权限）
     */
    @PostMapping("/generate/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> generateScheduleForDays(@RequestParam Integer days) {
        log.info("【批量生成场地时间表】天数: {}", days);
        return venueScheduleService.generateScheduleForDays(days);
    }
    
    /**
     * 更新场地时间段状态（管理员权限）
     */
    @PutMapping("/{scheduleId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam Integer status,
            @RequestParam(required = false) Long reservationId) {
        log.info("【更新场地时间表状态】ID: {}, 状态: {}, 预约ID: {}", scheduleId, status, reservationId);
        return venueScheduleService.updateScheduleStatus(scheduleId, status, reservationId);
    }
    
    /**
     * 检查场地时间段是否可预约
     */
    @GetMapping("/check")
    public ResponseVo<Boolean> checkTimeSlotAvailable(
            @RequestParam Integer venueId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        log.info("【检查时间段可用性】场地: {}, 日期: {}, 时间: {}-{}", venueId, date, startTime, endTime);
        return venueScheduleService.checkTimeSlotAvailable(venueId, date, startTime, endTime);
    }
} 