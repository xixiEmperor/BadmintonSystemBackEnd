package com.wuli.badminton.util;

import com.wuli.badminton.enums.VenueStatusEnum;
import com.wuli.badminton.pojo.SpecialDateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 场地时间管理工具类
 */
@Slf4j
public class VenueScheduleUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成标准时间段列表（每小时一个时段）
     * @param startHour 开始小时
     * @param endHour 结束小时
     * @return 时间段列表，每个元素包含开始时间和结束时间
     */
    public static List<Map<String, String>> generateTimeSlots(int startHour, int endHour) {
        List<Map<String, String>> timeSlots = new ArrayList<>();
        
        for (int hour = startHour; hour < endHour; hour++) {
            Map<String, String> timeSlot = new HashMap<>();
            timeSlot.put("startTime", String.format("%02d:00", hour));
            timeSlot.put("endTime", String.format("%02d:00", hour + 1));
            timeSlots.add(timeSlot);
        }
        
        return timeSlots;
    }
    
    /**
     * 根据日期和默认规则确定场地状态和可预约性
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 包含状态和可预约性的Map
     */
    public static Map<String, Integer> getDefaultVenueStatus(LocalDate date, String startTime, String endTime) {
        Map<String, Integer> result = new HashMap<>();
        
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
        
        // 周一至周五的规则
        if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
            // 工作日白天（08:00-18:00）使用中，不可预约
            if (start.getHour() >= 8 && start.getHour() < 18) {
                result.put("status", VenueStatusEnum.IN_USE.getCode());
                result.put("bookable", 0);
            }
            // 工作日晚上（18:00-21:00）空闲中，可预约
            else if (start.getHour() >= 18 && start.getHour() < 21) {
                result.put("status", VenueStatusEnum.AVAILABLE.getCode());
                result.put("bookable", 1);
            }
            // 其他时间不开放
            else {
                result.put("status", VenueStatusEnum.UNDER_MAINTENANCE.getCode());
                result.put("bookable", 0);
            }
        }
        // 周末规则（周六日）
        else {
            // 周末全天开放（08:00-21:00）
            if (start.getHour() >= 8 && start.getHour() < 21) {
                result.put("status", VenueStatusEnum.AVAILABLE.getCode());
                result.put("bookable", 1);
            }
            // 其他时间不开放
            else {
                result.put("status", VenueStatusEnum.UNDER_MAINTENANCE.getCode());
                result.put("bookable", 0);
            }
        }
        
        return result;
    }
    
    /**
     * 检查特殊日期配置是否影响指定场地和时间段
     * @param specialConfig 特殊日期配置
     * @param venueId 场地ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否受影响
     */
    public static boolean isAffectedBySpecialConfig(SpecialDateConfig specialConfig, Integer venueId, String startTime, String endTime) {
        // 检查场地是否受影响
        if (StringUtils.hasText(specialConfig.getAffectedVenueIds())) {
            String[] affectedVenueIds = specialConfig.getAffectedVenueIds().split(",");
            boolean venueAffected = false;
            for (String id : affectedVenueIds) {
                if (venueId.toString().equals(id.trim())) {
                    venueAffected = true;
                    break;
                }
            }
            if (!venueAffected) {
                return false;
            }
        }
        
        // 检查时间段是否受影响
        if (StringUtils.hasText(specialConfig.getStartTime()) && StringUtils.hasText(specialConfig.getEndTime())) {
            LocalTime configStart = LocalTime.parse(specialConfig.getStartTime(), TIME_FORMATTER);
            LocalTime configEnd = LocalTime.parse(specialConfig.getEndTime(), TIME_FORMATTER);
            LocalTime slotStart = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime slotEnd = LocalTime.parse(endTime, TIME_FORMATTER);
            
            // 检查时间段是否有重叠
            return !(slotEnd.isBefore(configStart) || slotStart.isAfter(configEnd) || slotStart.equals(configEnd));
        }
        
        return true; // 如果没有指定时间段，则影响全天
    }
    
    /**
     * 应用特殊日期配置到场地状态
     * @param defaultStatus 默认状态Map
     * @param specialConfig 特殊日期配置
     * @return 应用配置后的状态Map
     */
    public static Map<String, Integer> applySpecialConfig(Map<String, Integer> defaultStatus, SpecialDateConfig specialConfig) {
        Map<String, Integer> result = new HashMap<>(defaultStatus);
        result.put("status", specialConfig.getVenueStatus());
        result.put("bookable", specialConfig.getBookable());
        return result;
    }
    
    /**
     * 检查当前时间是否可以预约指定日期和时间段
     * @param reservationDate 预约日期
     * @param reservationTime 预约时间
     * @return 是否可以预约
     */
    public static boolean canBookAtCurrentTime(String reservationDate, String reservationTime) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalTime currentTime = now.toLocalTime();
            
            LocalDate targetDate = LocalDate.parse(reservationDate, DATE_FORMATTER);
            LocalTime targetTime = LocalTime.parse(reservationTime, TIME_FORMATTER);
            
            // 只能预约今天或未来的日期
            if (targetDate.isBefore(today)) {
                return false;
            }
            
            // 如果是今天，需要检查时间
            if (targetDate.equals(today)) {
                // 当天18:00后可以预约当天剩余的还未开始的时段
                if (currentTime.getHour() >= 18) {
                    return targetTime.isAfter(currentTime);
                }
                // 当天18:00前不能预约当天的时段
                else {
                    return false;
                }
            }
            
            // 如果是明天
            if (targetDate.equals(today.plusDays(1))) {
                // 前一天18:00后可以预约明天的时段
                return currentTime.getHour() >= 18;
            }
            
            // 如果是后天及以后，可以预约
            return true;
            
        } catch (Exception e) {
            log.error("【预约时间检查】时间解析错误", e);
            return false;
        }
    }
    
    /**
     * 检查是否在退款允许时间内
     * @param reservationDate 预约日期
     * @param reservationTime 预约时间
     * @return 是否可以退款（距离开场超过30分钟）
     */
    public static boolean canRefund(String reservationDate, String reservationTime) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reservationDateTime = LocalDateTime.of(
                LocalDate.parse(reservationDate, DATE_FORMATTER),
                LocalTime.parse(reservationTime, TIME_FORMATTER)
            );
            
            // 计算距离预约时间的分钟数
            long minutesUntilReservation = java.time.Duration.between(now, reservationDateTime).toMinutes();
            
            // 如果距离开场超过30分钟，可以退款
            return minutesUntilReservation > 30;
            
        } catch (Exception e) {
            log.error("【退款时间检查】时间解析错误", e);
            return false;
        }
    }
    
    /**
     * 格式化日期为字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    /**
     * 格式化日期时间为字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    
    /**
     * 解析日期字符串为Date对象
     */
    public static Date parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            log.error("【日期解析】解析失败: {}", dateStr, e);
            return null;
        }
    }
    
    /**
     * 获取未来几天的日期列表
     */
    public static List<String> getFutureDates(int days) {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            dates.add(today.plusDays(i).format(DATE_FORMATTER));
        }
        
        return dates;
    }
    
    /**
     * 检查时间段是否有重叠
     */
    public static boolean hasTimeConflict(String start1, String end1, String start2, String end2) {
        try {
            LocalTime startTime1 = LocalTime.parse(start1, TIME_FORMATTER);
            LocalTime endTime1 = LocalTime.parse(end1, TIME_FORMATTER);
            LocalTime startTime2 = LocalTime.parse(start2, TIME_FORMATTER);
            LocalTime endTime2 = LocalTime.parse(end2, TIME_FORMATTER);
            
            // 检查是否有重叠：两个时间段不重叠的条件是一个的结束时间小于等于另一个的开始时间
            return !(endTime1.isBefore(startTime2) || endTime1.equals(startTime2) || 
                     endTime2.isBefore(startTime1) || endTime2.equals(startTime1));
        } catch (Exception e) {
            log.error("【时间冲突检查】时间解析错误", e);
            return true; // 解析错误时，保守返回有冲突
        }
    }
} 