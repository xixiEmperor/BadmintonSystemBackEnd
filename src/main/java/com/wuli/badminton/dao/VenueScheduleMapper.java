package com.wuli.badminton.dao;

import com.wuli.badminton.dto.VenueScheduleQueryDto;
import com.wuli.badminton.pojo.VenueSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 场地时间表Mapper接口
 */
@Mapper
public interface VenueScheduleMapper {
    
    /**
     * 插入场地时间表记录
     */
    int insert(VenueSchedule venueSchedule);
    
    /**
     * 根据ID查询
     */
    VenueSchedule selectById(@Param("id") Long id);
    
    /**
     * 根据条件查询场地时间表
     */
    List<VenueSchedule> selectByCondition(VenueScheduleQueryDto queryDto);
    
    /**
     * 查询特定场地在特定日期和时间段的记录
     */
    VenueSchedule selectByVenueAndDateTime(
            @Param("venueId") Integer venueId,
            @Param("scheduleDate") Date scheduleDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
    
    /**
     * 更新场地时间表状态
     */
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("reservationId") Long reservationId);
    
    /**
     * 更新场地时间表
     */
    int updateById(VenueSchedule venueSchedule);
    
    /**
     * 批量插入场地时间表记录
     */
    int batchInsert(@Param("schedules") List<VenueSchedule> schedules);
    
    /**
     * 删除指定日期之前的记录
     */
    int deleteBeforeDate(@Param("date") Date date);
    
    /**
     * 查询指定日期范围内的场地时间表
     */
    List<VenueSchedule> selectByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("venueId") Integer venueId
    );
    
    /**
     * 根据预约订单ID查询
     */
    List<VenueSchedule> selectByReservationId(@Param("reservationId") Long reservationId);
} 