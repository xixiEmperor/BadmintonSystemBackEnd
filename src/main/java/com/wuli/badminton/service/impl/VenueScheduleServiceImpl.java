package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.VenueMapper;
import com.wuli.badminton.dao.VenueScheduleMapper;
import com.wuli.badminton.dto.VenueScheduleQueryDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.enums.VenueStatusEnum;
import com.wuli.badminton.pojo.SpecialDateConfig;
import com.wuli.badminton.pojo.Venue;
import com.wuli.badminton.pojo.VenueSchedule;
import com.wuli.badminton.service.SpecialDateConfigService;
import com.wuli.badminton.service.VenueScheduleService;
import com.wuli.badminton.util.VenueScheduleUtil;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueScheduleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * 场地时间表服务实现类
 */
@Service
@Slf4j
public class VenueScheduleServiceImpl implements VenueScheduleService {
    
    @Autowired
    private VenueScheduleMapper venueScheduleMapper;
    
    @Autowired
    private VenueMapper venueMapper;
    
    @Autowired
    private SpecialDateConfigService specialDateConfigService;
    
    @Override
    public ResponseVo<Map<String, Object>> getVenueScheduleMatrix(VenueScheduleQueryDto queryDto) {
        log.info("【获取场地预约矩阵】查询日期: {}", queryDto.getScheduleDate());
        
        List<VenueSchedule> schedules = venueScheduleMapper.selectByCondition(queryDto);
        List<Venue> venues = venueMapper.selectAll();
        
        // 构建矩阵数据
        Map<String, Object> matrix = new HashMap<>();
        
        // 场地列表
        List<Map<String, Object>> venueList = new ArrayList<>();
        for (Venue venue : venues) {
            Map<String, Object> venueInfo = new HashMap<>();
            venueInfo.put("id", venue.getId());
            venueInfo.put("name", venue.getName());
            venueInfo.put("status", venue.getStatus());
            venueList.add(venueInfo);
        }
        matrix.put("venues", venueList);
        
        // 时间段列表（8:00-21:00，每小时一个时段）
        List<Map<String, String>> timeSlots = VenueScheduleUtil.generateTimeSlots(8, 21);
        matrix.put("timeSlots", timeSlots);
        
        // 场地时间表数据
        Map<String, Map<String, VenueScheduleVo>> scheduleMatrix = new HashMap<>();
        for (VenueSchedule schedule : schedules) {
            String key = schedule.getVenueId() + "_" + schedule.getStartTime();
            VenueScheduleVo scheduleVo = convertToVenueScheduleVo(schedule);
            
            scheduleMatrix.computeIfAbsent(schedule.getVenueId().toString(), k -> new HashMap<>())
                    .put(schedule.getStartTime(), scheduleVo);
        }
        matrix.put("scheduleMatrix", scheduleMatrix);
        
        log.info("【获取场地预约矩阵】查询成功，场地数: {}, 时间段数: {}", venues.size(), timeSlots.size());
        return ResponseVo.success(matrix);
    }
    
    @Override
    public ResponseVo<List<VenueScheduleVo>> getVenueSchedules(VenueScheduleQueryDto queryDto) {
        log.info("【查询场地时间表】条件: {}", queryDto);
        
        List<VenueSchedule> schedules = venueScheduleMapper.selectByCondition(queryDto);
        List<VenueScheduleVo> scheduleVos = new ArrayList<>();
        
        for (VenueSchedule schedule : schedules) {
            VenueScheduleVo scheduleVo = convertToVenueScheduleVo(schedule);
            scheduleVos.add(scheduleVo);
        }
        
        log.info("【查询场地时间表】查询成功，共{}条记录", scheduleVos.size());
        return ResponseVo.success(scheduleVos);
    }
    
    @Override
    @Transactional
    public ResponseVo<String> generateScheduleForDate(String date) {
        log.info("【生成场地时间表】日期: {}", date);
        
        Date scheduleDate = VenueScheduleUtil.parseDate(date);
        if (scheduleDate == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "日期格式错误");
        }
        
        LocalDate localDate = LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 获取所有可用场地
        List<Venue> venues = venueMapper.selectByStatus(1);
        if (venues.isEmpty()) {
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST, "没有可用的场地");
        }
        
        // 获取该日期的特殊配置
        List<SpecialDateConfig> specialConfigs = specialDateConfigService.getConfigsByDate(date);
        
        // 生成时间段（8:00-21:00）
        List<Map<String, String>> timeSlots = VenueScheduleUtil.generateTimeSlots(8, 21);
        
        List<VenueSchedule> schedules = new ArrayList<>();
        Date now = new Date();
        
        for (Venue venue : venues) {
            for (Map<String, String> timeSlot : timeSlots) {
                String startTime = timeSlot.get("startTime");
                String endTime = timeSlot.get("endTime");
                
                // 检查是否已存在该时间段的记录
                VenueSchedule existingSchedule = venueScheduleMapper.selectByVenueAndDateTime(
                        venue.getId(), scheduleDate, startTime, endTime);
                
                if (existingSchedule != null) {
                    continue; // 已存在，跳过
                }
                
                // 获取默认状态
                Map<String, Integer> defaultStatus = VenueScheduleUtil.getDefaultVenueStatus(localDate, startTime, endTime);
                
                // 应用特殊配置
                for (SpecialDateConfig specialConfig : specialConfigs) {
                    if (specialConfig.getEnabled() == 1 && 
                        VenueScheduleUtil.isAffectedBySpecialConfig(specialConfig, venue.getId(), startTime, endTime)) {
                        defaultStatus = VenueScheduleUtil.applySpecialConfig(defaultStatus, specialConfig);
                        break; // 只应用第一个匹配的特殊配置
                    }
                }
                
                // 创建时间表记录
                VenueSchedule schedule = new VenueSchedule();
                schedule.setVenueId(venue.getId());
                schedule.setVenueName(venue.getName());
                schedule.setScheduleDate(scheduleDate);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setStatus(defaultStatus.get("status"));
                schedule.setBookable(defaultStatus.get("bookable"));
                schedule.setCreateTime(now);
                schedule.setUpdateTime(now);
                
                schedules.add(schedule);
            }
        }
        
        if (!schedules.isEmpty()) {
            int result = venueScheduleMapper.batchInsert(schedules);
            log.info("【生成场地时间表】成功生成{}条记录", result);
            return ResponseVo.success("成功生成" + result + "条时间表记录");
        } else {
            log.info("【生成场地时间表】该日期时间表已存在，无需重复生成");
            return ResponseVo.success("该日期时间表已存在");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> generateScheduleForDays(Integer days) {
        log.info("【批量生成场地时间表】天数: {}", days);
        
        if (days == null || days <= 0) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "天数必须大于0");
        }
        
        List<String> dates = VenueScheduleUtil.getFutureDates(days);
        int totalGenerated = 0;
        
        for (String date : dates) {
            ResponseVo<String> result = generateScheduleForDate(date);
            if (result.getCode() == 0) {
                // 解析生成的数量（简单处理）
                if (result.getData().contains("成功生成")) {
                    totalGenerated++;
                }
            }
        }
        
        log.info("【批量生成场地时间表】完成，共处理{}天，生成{}天的时间表", days, totalGenerated);
        return ResponseVo.success("批量生成完成，共处理" + days + "天");
    }
    
    @Override
    public ResponseVo<String> updateScheduleStatus(Long scheduleId, Integer status, Long reservationId) {
        log.info("【更新场地时间表状态】ID: {}, 状态: {}, 预约ID: {}", scheduleId, status, reservationId);
        
        VenueSchedule schedule = venueScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "场地时间表记录不存在");
        }
        
        int result = venueScheduleMapper.updateStatusById(scheduleId, status, reservationId);
        
        if (result > 0) {
            log.info("【更新场地时间表状态】更新成功");
            return ResponseVo.success("状态更新成功");
        } else {
            log.error("【更新场地时间表状态】更新失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "状态更新失败");
        }
    }
    
    @Override
    public ResponseVo<Boolean> checkTimeSlotAvailable(Integer venueId, String date, String startTime, String endTime) {
        log.info("【检查时间段可用性】场地: {}, 日期: {}, 时间: {}-{}", venueId, date, startTime, endTime);
        
        Date scheduleDate = VenueScheduleUtil.parseDate(date);
        if (scheduleDate == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "日期格式错误");
        }
        
        VenueSchedule schedule = venueScheduleMapper.selectByVenueAndDateTime(venueId, scheduleDate, startTime, endTime);
        
        boolean available = false;
        if (schedule != null) {
            // 检查状态是否为空闲中且可预约
            available = schedule.getStatus().equals(VenueStatusEnum.AVAILABLE.getCode()) && 
                       schedule.getBookable() == 1;
        }
        
        log.info("【检查时间段可用性】结果: {}", available);
        return ResponseVo.success(available);
    }
    
    /**
     * 转换为VenueScheduleVo
     */
    private VenueScheduleVo convertToVenueScheduleVo(VenueSchedule schedule) {
        VenueScheduleVo scheduleVo = new VenueScheduleVo();
        BeanUtils.copyProperties(schedule, scheduleVo);
        
        // 格式化日期
        if (schedule.getScheduleDate() != null) {
            scheduleVo.setScheduleDate(VenueScheduleUtil.formatDate(schedule.getScheduleDate()));
        }
        
        // 设置时间段描述
        if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
            scheduleVo.setTimeSlot(schedule.getStartTime() + "-" + schedule.getEndTime());
        }
        
        // 设置状态描述
        VenueStatusEnum statusEnum = VenueStatusEnum.getByCode(schedule.getStatus());
        if (statusEnum != null) {
            scheduleVo.setStatusDesc(statusEnum.getDesc());
        }
        
        // 设置可预约描述
        scheduleVo.setBookableDesc(schedule.getBookable() == 1 ? "可预约" : "不可预约");
        
        return scheduleVo;
    }
} 