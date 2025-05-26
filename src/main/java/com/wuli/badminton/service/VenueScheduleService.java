package com.wuli.badminton.service;

import com.wuli.badminton.dto.VenueScheduleQueryDto;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueScheduleVo;
import java.util.List;
import java.util.Map;

/**
 * 场地时间表服务接口
 */
public interface VenueScheduleService {
    
    /**
     * 查询场地预约情况矩阵图
     * 返回指定日期所有场地的时间段状态
     */
    ResponseVo<Map<String, Object>> getVenueScheduleMatrix(VenueScheduleQueryDto queryDto);
    
    /**
     * 根据条件查询场地时间表
     */
    ResponseVo<List<VenueScheduleVo>> getVenueSchedules(VenueScheduleQueryDto queryDto);
    
    /**
     * 生成指定日期的场地时间表
     * 根据默认规则和特殊日期配置生成
     */
    ResponseVo<String> generateScheduleForDate(String date);
    
    /**
     * 批量生成未来几天的场地时间表
     */
    ResponseVo<String> generateScheduleForDays(Integer days);
    
    /**
     * 更新场地时间段状态
     */
    ResponseVo<String> updateScheduleStatus(Long scheduleId, Integer status, Long reservationId);
    
    /**
     * 检查场地时间段是否可预约
     */
    ResponseVo<Boolean> checkTimeSlotAvailable(Integer venueId, String date, String startTime, String endTime);
} 