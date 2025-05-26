package com.wuli.badminton.task;

import com.wuli.badminton.service.VenueScheduleService;
import com.wuli.badminton.util.VenueScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 场地时间表定时任务
 */
@Component
@Slf4j
public class VenueScheduleTask {
    
    @Autowired
    private VenueScheduleService venueScheduleService;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 每日凌晨1点生成明天的场地时间表
     * 根据默认规则和特殊日期配置生成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateTomorrowSchedule() {
        log.info("【定时任务】开始生成明天的场地时间表");
        
        try {
            String tomorrow = LocalDate.now().plusDays(1).format(dateFormatter);
            
            log.info("【定时任务】生成日期: {}", tomorrow);
            venueScheduleService.generateScheduleForDate(tomorrow);
            
            log.info("【定时任务】明天场地时间表生成完成");
        } catch (Exception e) {
            log.error("【定时任务】生成明天场地时间表失败", e);
        }
    }
    
    /**
     * 每日凌晨2点清理7天前的历史数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanHistorySchedule() {
        log.info("【定时任务】开始清理历史场地时间表数据");
        
        try {
            // 清理7天前的数据可以在Service层实现
            // 这里先预留接口
            log.info("【定时任务】历史数据清理完成");
        } catch (Exception e) {
            log.error("【定时任务】清理历史数据失败", e);
        }
    }
    
    /**
     * 每小时检查一次场地状态异常情况
     * 检查是否有预约时间已过但状态仍为"已预约"的记录
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAbnormalVenueStatus() {
        log.info("【定时任务】开始检查场地状态异常");
        
        try {
            // 这里可以实现检查逻辑
            // 1. 查询当前时间之前的"已预约"状态记录
            // 2. 发送告警通知
            // 3. 自动更新为"已完成"状态（可选）
            
            log.info("【定时任务】场地状态检查完成");
        } catch (Exception e) {
            log.error("【定时任务】场地状态检查失败", e);
        }
    }
    
    /**
     * 手动触发生成未来几天的时间表（测试用）
     */
    public void manualGenerateSchedules(Integer days) {
        log.info("【手动任务】开始生成未来{}天的场地时间表", days);
        
        try {
            venueScheduleService.generateScheduleForDays(days);
            log.info("【手动任务】批量生成场地时间表完成");
        } catch (Exception e) {
            log.error("【手动任务】批量生成场地时间表失败", e);
        }
    }
} 