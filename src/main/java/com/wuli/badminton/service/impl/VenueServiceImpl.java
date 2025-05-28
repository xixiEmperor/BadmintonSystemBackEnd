package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.VenueMapper;
import com.wuli.badminton.dao.ReservationOrderMapper;
import com.wuli.badminton.dao.SpecialDateConfigMapper;
import com.wuli.badminton.dto.VenueDto;
import com.wuli.badminton.dto.VenueStatusMatrixDto;
import com.wuli.badminton.dto.VenueAvailabilityQueryDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.enums.VenueStatusEnum;
import com.wuli.badminton.enums.ReservationStatusEnum;
import com.wuli.badminton.pojo.Venue;
import com.wuli.badminton.pojo.ReservationOrder;
import com.wuli.badminton.pojo.SpecialDateConfig;
import com.wuli.badminton.service.VenueService;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueVo;
import com.wuli.badminton.vo.VenueStatusMatrixVo;
import com.wuli.badminton.vo.VenueAvailabilityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Calendar;


/**
 * 场地服务实现类
 */
@Service
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    @Autowired
    private VenueMapper venueMapper;
    
    @Autowired
    private ReservationOrderMapper reservationOrderMapper;
    
    @Autowired
    private SpecialDateConfigMapper specialDateConfigMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ResponseVo<List<VenueVo>> getAllVenues() {
        log.info("【获取所有场地】开始查询");
        
        List<Venue> venues = venueMapper.selectAll();
        List<VenueVo> venueVos = new ArrayList<>();
        
        for (Venue venue : venues) {
            VenueVo venueVo = convertToVenueVo(venue);
            venueVos.add(venueVo);
        }
        
        log.info("【获取所有场地】查询成功，共{}个场地", venueVos.size());
        return ResponseVo.success(venueVos);
    }
    
    @Override
    public ResponseVo<VenueVo> getVenueById(Integer id) {
        log.info("【获取场地详情】场地ID: {}", id);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【获取场地详情】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        VenueVo venueVo = convertToVenueVo(venue);
        
        log.info("【获取场地详情】查询成功，场地名称: {}", venue.getName());
        return ResponseVo.success(venueVo);
    }
    
    @Override
    public ResponseVo<List<VenueVo>> getVenuesByStatus(Integer status) {
        log.info("【根据状态获取场地】状态: {}", status);
        
        List<Venue> venues = venueMapper.selectByStatus(status);
        List<VenueVo> venueVos = new ArrayList<>();
        
        for (Venue venue : venues) {
            VenueVo venueVo = convertToVenueVo(venue);
            venueVos.add(venueVo);
        }
        
        log.info("【根据状态获取场地】查询成功，共{}个场地", venueVos.size());
        return ResponseVo.success(venueVos);
    }
    
    @Override
    @Transactional
    public ResponseVo<String> addVenue(VenueDto venueDto) {
        log.info("【新增场地】场地名称: {}", venueDto.getName());
        
        // 转换为实体对象
        Venue venue = new Venue();
        BeanUtils.copyProperties(venueDto, venue);
        
        Date now = new Date();
        venue.setCreateTime(now);
        venue.setUpdateTime(now);
        
        // 设置默认状态为启用
        if (venue.getStatus() == null) {
            venue.setStatus(1);
        }
        
        int result = venueMapper.insert(venue);
        
        if (result > 0) {
            log.info("【新增场地】新增成功，场地ID: {}", venue.getId());
            return ResponseVo.success("场地新增成功");
        } else {
            log.error("【新增场地】新增失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地新增失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> updateVenue(Integer id, VenueDto venueDto) {
        log.info("【更新场地信息】场地ID: {}, 场地名称: {}", id, venueDto.getName());
        
        Venue existingVenue = venueMapper.selectById(id);
        if (existingVenue == null) {
            log.warn("【更新场地信息】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 更新场地信息
        existingVenue.setName(venueDto.getName());
        existingVenue.setDescription(venueDto.getDescription());
        existingVenue.setLocation(venueDto.getLocation());
        existingVenue.setPricePerHour(venueDto.getPricePerHour());
        existingVenue.setType(venueDto.getType());
        if (venueDto.getStatus() != null) {
            existingVenue.setStatus(venueDto.getStatus());
        }
        existingVenue.setUpdateTime(new Date());
        
        int result = venueMapper.updateById(existingVenue);
        
        if (result > 0) {
            log.info("【更新场地信息】更新成功，场地: {}", existingVenue.getName());
            return ResponseVo.success("场地信息更新成功");
        } else {
            log.error("【更新场地信息】更新失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地信息更新失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> updateVenueStatus(Integer id, Integer status) {
        log.info("【更新场地状态】场地ID: {}, 新状态: {}", id, status);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【更新场地状态】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 验证状态值只能是0或1
        if (status == null || (status != 0 && status != 1)) {
            log.warn("【更新场地状态】无效的状态值: {}", status);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "场地状态只能是0(未启用)或1(启用)");
        }
        
        int result = venueMapper.updateStatus(id, status);
        
        if (result > 0) {
            String statusDesc = status == 1 ? "启用" : "未启用";
            log.info("【更新场地状态】更新成功，场地: {}, 状态: {}", venue.getName(), statusDesc);
            return ResponseVo.success("场地状态更新成功");
        } else {
            log.error("【更新场地状态】更新失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地状态更新失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> deleteVenue(Integer id) {
        log.info("【删除场地】场地ID: {}", id);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【删除场地】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 检查场地是否可以删除（如果有未完成的预约，不允许删除）
        // TODO: 这里可以添加预约检查逻辑
        
        int result = venueMapper.deleteById(id);
        
        if (result > 0) {
            log.info("【删除场地】删除成功，场地: {}", venue.getName());
            return ResponseVo.success("场地删除成功");
        } else {
            log.error("【删除场地】删除失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地删除失败");
        }
    }
    
    @Override
    public ResponseVo<VenueStatusMatrixVo> getVenueStatusMatrix(VenueStatusMatrixDto dto) {
        log.info("【获取场地状态矩阵】日期: {}, 场地ID: {}", dto.getDate(), dto.getVenueId());
        
        try {
            Date queryDate = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
            
            // 1. 获取场地列表
            List<Venue> venues;
            if (dto.getVenueId() != null) {
                Venue venue = venueMapper.selectById(dto.getVenueId());
                if (venue == null) {
                    return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
                }
                venues = Arrays.asList(venue);
            } else {
                venues = venueMapper.selectAll();
            }
            
            // 2. 获取时间段列表
            List<String> timeSlots = getTimeSlots(queryDate);
            
            // 3. 获取预约记录
            Map<Integer, List<ReservationOrder>> venueReservations = getVenueReservations(venues, queryDate);
            
            // 4. 获取特殊日期配置
            List<SpecialDateConfig> specialConfigs = getSpecialDateConfigs(queryDate);
            
            // 5. 构建响应数据
            VenueStatusMatrixVo result = new VenueStatusMatrixVo();
            result.setDate(dto.getDate());
            
            // 设置场地信息
            List<VenueStatusMatrixVo.VenueInfo> venueInfos = venues.stream()
                    .map(this::convertToVenueInfo)
                    .collect(Collectors.toList());
            result.setVenues(venueInfos);
            
            // 设置时间段
            result.setTimeSlots(timeSlots);
            
            // 构建状态矩阵
            Map<String, Map<String, VenueStatusMatrixVo.TimeSlotStatus>> statusMatrix = new HashMap<>();
            for (Venue venue : venues) {
                Map<String, VenueStatusMatrixVo.TimeSlotStatus> venueSlots = new HashMap<>();
                for (String timeSlot : timeSlots) {
                    VenueStatusMatrixVo.TimeSlotStatus slotStatus = calculateTimeSlotStatus(
                            venue, timeSlot, queryDate, venueReservations.get(venue.getId()), specialConfigs);
                    venueSlots.put(timeSlot, slotStatus);
                }
                statusMatrix.put(venue.getId().toString(), venueSlots);
            }
            result.setStatusMatrix(statusMatrix);
            
            log.info("【获取场地状态矩阵】查询成功，场地数: {}, 时间段数: {}", venues.size(), timeSlots.size());
            return ResponseVo.success(result);
            
        } catch (ParseException e) {
            log.error("【获取场地状态矩阵】日期解析错误", e);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "日期格式错误");
        } catch (Exception e) {
            log.error("【获取场地状态矩阵】查询失败", e);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "查询失败");
        }
    }
    
    /**
     * 获取时间段列表
     */
    private List<String> getTimeSlots(Date queryDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(queryDate);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        List<String> timeSlots = new ArrayList<>();
        
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            // 周末：08:00-21:00 可预约
            for (int hour = 8; hour < 21; hour++) {
                timeSlots.add(String.format("%02d:00-%02d:00", hour, hour + 1));
            }
        } else {
            // 工作日：全天显示，但18:00前为教学时间
            for (int hour = 8; hour < 21; hour++) {
                timeSlots.add(String.format("%02d:00-%02d:00", hour, hour + 1));
            }
        }
        
        return timeSlots;
    }
    
    /**
     * 获取场地预约记录
     */
    private Map<Integer, List<ReservationOrder>> getVenueReservations(List<Venue> venues, Date queryDate) {
        Map<Integer, List<ReservationOrder>> venueReservations = new HashMap<>();
        
        for (Venue venue : venues) {
            List<ReservationOrder> reservations = reservationOrderMapper.selectByVenueAndDate(venue.getId(), queryDate);
            // 只考虑有效状态的订单（待支付、已支付、已完成）
            List<ReservationOrder> validReservations = reservations.stream()
                    .filter(order -> Arrays.asList(
                            ReservationStatusEnum.PENDING_PAYMENT.getCode(),
                            ReservationStatusEnum.PAID.getCode(),
                            ReservationStatusEnum.COMPLETED.getCode()
                    ).contains(order.getStatus()))
                    .collect(Collectors.toList());
            venueReservations.put(venue.getId(), validReservations);
        }
        
        return venueReservations;
    }
    
    /**
     * 获取特殊日期配置
     */
    private List<SpecialDateConfig> getSpecialDateConfigs(Date queryDate) {
        try {
            return specialDateConfigMapper.selectByDate(queryDate);
        } catch (Exception e) {
            log.warn("获取特殊日期配置失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 计算时间段状态
     */
    private VenueStatusMatrixVo.TimeSlotStatus calculateTimeSlotStatus(
            Venue venue, String timeSlot, Date queryDate, 
            List<ReservationOrder> reservations, List<SpecialDateConfig> specialConfigs) {
        
        VenueStatusMatrixVo.TimeSlotStatus status = new VenueStatusMatrixVo.TimeSlotStatus();
        
        // 1. 检查场地基础状态
        if (venue.getStatus() != 1) {
            status.setStatus(4);
            status.setStatusDesc("维护中");
            status.setBookable(false);
            status.setReason("场地未启用");
            return status;
        }
        
        // 2. 检查特殊日期配置
        SpecialDateConfig specialConfig = getMatchingSpecialConfig(venue.getId(), timeSlot, specialConfigs);
        if (specialConfig != null) {
            status.setStatus(specialConfig.getVenueStatus());
            status.setStatusDesc(getStatusDesc(specialConfig.getVenueStatus()));
            status.setBookable(specialConfig.getBookable() == 1);
            status.setReason(specialConfig.getDescription());
            return status;
        }
        
        // 3. 检查预约记录
        ReservationOrder conflictOrder = getConflictingReservation(timeSlot, reservations);
        if (conflictOrder != null) {
            // 根据订单状态和时间判断具体状态
            if (ReservationStatusEnum.COMPLETED.getCode().equals(conflictOrder.getStatus())) {
                // 订单已完成，说明用户已到场
                status.setStatus(2);
                status.setStatusDesc("使用中");
                status.setBookable(false);
                status.setReason("用户" + conflictOrder.getUsername() + "正在使用");
            } else if (ReservationStatusEnum.PAID.getCode().equals(conflictOrder.getStatus())) {
                // 订单已支付，但未完成，说明已预约但用户未到场
                status.setStatus(3);
                status.setStatusDesc("已预约");
                status.setBookable(false);
                status.setReason("用户" + conflictOrder.getUsername() + "预约");
            } else if (ReservationStatusEnum.PENDING_PAYMENT.getCode().equals(conflictOrder.getStatus())) {
                // 待支付订单也占用时段（重要修正）
                status.setStatus(3);
                status.setStatusDesc("已预约");
                status.setBookable(false);
                status.setReason("用户" + conflictOrder.getUsername() + "预约中（待支付）");
            } else {
                // 已取消、已关闭等状态不影响场地状态
                return calculateDefaultStatus(queryDate, timeSlot);
            }
            status.setReservationId(conflictOrder.getId());
            status.setUsername(conflictOrder.getUsername());
            return status;
        }
        
        // 4. 应用默认规则
        return calculateDefaultStatus(queryDate, timeSlot);
    }
    
    /**
     * 计算默认状态（没有预约冲突时）
     */
    private VenueStatusMatrixVo.TimeSlotStatus calculateDefaultStatus(Date queryDate, String timeSlot) {
        VenueStatusMatrixVo.TimeSlotStatus status = new VenueStatusMatrixVo.TimeSlotStatus();
        
        // 应用默认规则
        Calendar cal = Calendar.getInstance();
        cal.setTime(queryDate);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        String[] times = timeSlot.split("-");
        int hour = Integer.parseInt(times[0].split(":")[0]);
        
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && hour < 18) {
            // 工作日18:00前为教学时间
            status.setStatus(2);
            status.setStatusDesc("使用中");
            status.setBookable(false);
            status.setReason("教学时间");
        } else {
            // 空闲时间
            status.setStatus(1);
            status.setStatusDesc("空闲中");
            status.setBookable(true);
            status.setReason(null);
        }
        
        return status;
    }
    
    /**
     * 查找匹配的特殊日期配置
     */
    private SpecialDateConfig getMatchingSpecialConfig(Integer venueId, String timeSlot, List<SpecialDateConfig> configs) {
        for (SpecialDateConfig config : configs) {
            if (config.getEnabled() != 1) continue;
            
            // 检查是否影响该场地
            if (config.getAffectedVenueIds() != null && !config.getAffectedVenueIds().isEmpty()) {
                List<String> venueIds = Arrays.asList(config.getAffectedVenueIds().split(","));
                if (!venueIds.contains(venueId.toString())) {
                    continue;
                }
            }
            
            // 检查时间段
            if (config.getStartTime() != null && config.getEndTime() != null) {
                String[] slotTimes = timeSlot.split("-");
                String slotStart = slotTimes[0];
                String slotEnd = slotTimes[1];
                
                if (slotStart.compareTo(config.getEndTime()) >= 0 || slotEnd.compareTo(config.getStartTime()) <= 0) {
                    continue; // 时间不重叠
                }
            }
            
            return config;
        }
        return null;
    }
    
    /**
     * 查找冲突的预约记录
     */
    private ReservationOrder getConflictingReservation(String timeSlot, List<ReservationOrder> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return null;
        }
        
        String[] slotTimes = timeSlot.split("-");
        String slotStart = slotTimes[0];
        String slotEnd = slotTimes[1];
        
        for (ReservationOrder reservation : reservations) {
            // 检查时间冲突
            if (!(slotEnd.compareTo(reservation.getStartTime()) <= 0 || 
                  slotStart.compareTo(reservation.getEndTime()) >= 0)) {
                return reservation;
            }
        }
        
        return null;
    }
    
    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        VenueStatusEnum statusEnum = VenueStatusEnum.getByCode(status);
        return statusEnum != null ? statusEnum.getDesc() : "未知";
    }
    
    /**
     * 转换为场地信息VO
     */
    private VenueStatusMatrixVo.VenueInfo convertToVenueInfo(Venue venue) {
        VenueStatusMatrixVo.VenueInfo venueInfo = new VenueStatusMatrixVo.VenueInfo();
        venueInfo.setId(venue.getId());
        venueInfo.setName(venue.getName());
        venueInfo.setLocation(venue.getLocation());
        venueInfo.setPricePerHour(venue.getPricePerHour());
        venueInfo.setIsAvailable(venue.getStatus() == 1);
        return venueInfo;
    }
    
    /**
     * 转换为VenueVo
     */
    private VenueVo convertToVenueVo(Venue venue) {
        VenueVo venueVo = new VenueVo();
        BeanUtils.copyProperties(venue, venueVo);
        
        // 设置类型描述
        venueVo.setTypeDesc(venue.getType() == 1 ? "羽毛球场" : "未知");
        
        // 设置状态描述
        venueVo.setStatusDesc(venue.getStatus() == 1 ? "启用" : "未启用");
        
        // 格式化时间
        if (venue.getCreateTime() != null) {
            venueVo.setCreateTime(dateFormat.format(venue.getCreateTime()));
        }
        if (venue.getUpdateTime() != null) {
            venueVo.setUpdateTime(dateFormat.format(venue.getUpdateTime()));
        }
        
        return venueVo;
    }

    @Override
    public ResponseVo<VenueAvailabilityVo> queryVenueAvailability(VenueAvailabilityQueryDto dto) {
        log.info("【查询场地可用性】日期: {}, 时间段: {}-{}", dto.getDate(), dto.getStartTime(), dto.getEndTime());
        
        try {
            // 1. 参数验证
            Date queryDate = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
            
            // 验证时间段
            if (dto.getStartTime().compareTo(dto.getEndTime()) >= 0) {
                return ResponseVo.error(ResponseEnum.PARAM_ERROR, "开始时间必须早于结束时间");
            }
            
            // 2. 获取所有场地（应用筛选条件）
            List<Venue> allVenues = getFilteredVenues(dto);
            
            // 3. 构建查询的时间段
            String timeSlot = dto.getStartTime() + "-" + dto.getEndTime();
            
            // 4. 获取预约记录
            Map<Integer, List<ReservationOrder>> venueReservations = getVenueReservations(allVenues, queryDate);
            
            // 5. 获取特殊日期配置
            List<SpecialDateConfig> specialConfigs = getSpecialDateConfigs(queryDate);
            
            // 6. 分析每个场地的可用性
            List<VenueAvailabilityVo.AvailableVenue> availableVenues = new ArrayList<>();
            List<VenueAvailabilityVo.UnavailableVenue> unavailableVenues = new ArrayList<>();
            
            for (Venue venue : allVenues) {
                VenueAvailabilityResult result = analyzeVenueAvailability(
                        venue, dto.getStartTime(), dto.getEndTime(), queryDate, 
                        venueReservations.get(venue.getId()), specialConfigs);
                
                if (result.isAvailable()) {
                    VenueAvailabilityVo.AvailableVenue availableVenue = convertToAvailableVenue(venue, result);
                    availableVenues.add(availableVenue);
                } else {
                    VenueAvailabilityVo.UnavailableVenue unavailableVenue = convertToUnavailableVenue(venue, result);
                    unavailableVenues.add(unavailableVenue);
                }
            }
            
            // 7. 对可用场地进行排序（按价格排序）
            availableVenues.sort((v1, v2) -> v1.getPricePerHour().compareTo(v2.getPricePerHour()));
            
            // 8. 构建响应
            VenueAvailabilityVo result = new VenueAvailabilityVo();
            result.setDate(dto.getDate());
            result.setTimeSlot(timeSlot);
            result.setTotalVenues(allVenues.size());
            result.setTotalAvailable(availableVenues.size());
            result.setAvailableVenues(availableVenues);
            result.setUnavailableVenues(unavailableVenues);
            
            log.info("【查询场地可用性】查询成功，总场地数: {}, 可用场地数: {}", 
                    allVenues.size(), availableVenues.size());
            return ResponseVo.success(result);
            
        } catch (ParseException e) {
            log.error("【查询场地可用性】日期解析错误", e);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "日期格式错误");
        } catch (Exception e) {
            log.error("【查询场地可用性】查询失败", e);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "查询失败");
        }
    }
    
    /**
     * 获取筛选后的场地列表
     */
    private List<Venue> getFilteredVenues(VenueAvailabilityQueryDto dto) {
        List<Venue> venues = venueMapper.selectAll();
        
        return venues.stream()
                .filter(venue -> {
                    // 只选择启用状态的场地
                    if (venue.getStatus() != 1) {
                        return false;
                    }
                    
                    // 场地类型筛选
                    if (dto.getVenueType() != null && !dto.getVenueType().equals(venue.getType())) {
                        return false;
                    }
                    
                    // 价格筛选
                    if (dto.getMinPrice() != null && venue.getPricePerHour().doubleValue() < dto.getMinPrice()) {
                        return false;
                    }
                    
                    if (dto.getMaxPrice() != null && venue.getPricePerHour().doubleValue() > dto.getMaxPrice()) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 分析场地在指定时间段的可用性
     */
    private VenueAvailabilityResult analyzeVenueAvailability(
            Venue venue, String startTime, String endTime, Date queryDate,
            List<ReservationOrder> reservations, List<SpecialDateConfig> specialConfigs) {
        
        VenueAvailabilityResult result = new VenueAvailabilityResult();
        result.setVenue(venue);
        
        // 1. 检查特殊日期配置
        SpecialDateConfig conflictConfig = getConflictingSpecialConfig(
                venue.getId(), startTime, endTime, specialConfigs);
        if (conflictConfig != null) {
            result.setAvailable(conflictConfig.getBookable() == 1);
            result.setReason(conflictConfig.getDescription());
            result.setStatus(conflictConfig.getVenueStatus());
            return result;
        }
        
        // 2. 检查预约冲突
        ReservationOrder conflictOrder = getConflictingReservationInTimeRange(
                startTime, endTime, reservations);
        if (conflictOrder != null) {
            result.setAvailable(false);
            result.setReason("该时间段已被用户" + conflictOrder.getUsername() + "预约");
            result.setStatus(3); // 已预约
            return result;
        }
        
        // 3. 检查默认业务规则
        boolean isWeekend = isWeekend(queryDate);
        boolean isTeachingTime = isTeachingTime(startTime, endTime, isWeekend);
        
        if (isTeachingTime) {
            result.setAvailable(false);
            result.setReason("该时间段为教学时间");
            result.setStatus(2); // 使用中
        } else {
            result.setAvailable(true);
            result.setReason(null);
            result.setStatus(1); // 空闲中
        }
        
        return result;
    }
    
    /**
     * 检查特殊日期配置冲突
     */
    private SpecialDateConfig getConflictingSpecialConfig(
            Integer venueId, String startTime, String endTime, List<SpecialDateConfig> configs) {
        
        for (SpecialDateConfig config : configs) {
            if (config.getEnabled() != 1) continue;
            
            // 检查是否影响该场地
            if (config.getAffectedVenueIds() != null && !config.getAffectedVenueIds().isEmpty()) {
                List<String> venueIds = Arrays.asList(config.getAffectedVenueIds().split(","));
                if (!venueIds.contains(venueId.toString())) {
                    continue;
                }
            }
            
            // 检查时间段冲突
            if (config.getStartTime() != null && config.getEndTime() != null) {
                if (!(endTime.compareTo(config.getStartTime()) <= 0 || 
                      startTime.compareTo(config.getEndTime()) >= 0)) {
                    return config; // 时间有重叠
                }
            } else {
                // 如果没有设置时间段，则影响全天
                return config;
            }
        }
        
        return null;
    }
    
    /**
     * 检查预约冲突（时间范围）
     */
    private ReservationOrder getConflictingReservationInTimeRange(
            String startTime, String endTime, List<ReservationOrder> reservations) {
        
        if (reservations == null || reservations.isEmpty()) {
            return null;
        }
        
        for (ReservationOrder reservation : reservations) {
            // 检查时间冲突
            if (!(endTime.compareTo(reservation.getStartTime()) <= 0 || 
                  startTime.compareTo(reservation.getEndTime()) >= 0)) {
                return reservation;
            }
        }
        
        return null;
    }
    
    /**
     * 判断是否为周末
     */
    private boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    /**
     * 判断是否为教学时间
     */
    private boolean isTeachingTime(String startTime, String endTime, boolean isWeekend) {
        if (isWeekend) {
            return false; // 周末没有教学时间
        }
        
        // 工作日18:00前为教学时间
        return startTime.compareTo("18:00") < 0;
    }
    
    /**
     * 转换为可用场地VO
     */
    private VenueAvailabilityVo.AvailableVenue convertToAvailableVenue(
            Venue venue, VenueAvailabilityResult result) {
        
        VenueAvailabilityVo.AvailableVenue availableVenue = new VenueAvailabilityVo.AvailableVenue();
        availableVenue.setId(venue.getId());
        availableVenue.setName(venue.getName());
        availableVenue.setDescription(venue.getDescription());
        availableVenue.setLocation(venue.getLocation());
        availableVenue.setPricePerHour(venue.getPricePerHour());
        availableVenue.setType(venue.getType());
        availableVenue.setTypeDesc(venue.getType() == 1 ? "羽毛球场" : "未知");
        
        return availableVenue;
    }
    
    /**
     * 转换为不可用场地VO
     */
    private VenueAvailabilityVo.UnavailableVenue convertToUnavailableVenue(
            Venue venue, VenueAvailabilityResult result) {
        
        VenueAvailabilityVo.UnavailableVenue unavailableVenue = new VenueAvailabilityVo.UnavailableVenue();
        unavailableVenue.setId(venue.getId());
        unavailableVenue.setName(venue.getName());
        unavailableVenue.setLocation(venue.getLocation());
        unavailableVenue.setPricePerHour(venue.getPricePerHour());
        unavailableVenue.setUnavailableReason(result.getReason());
        unavailableVenue.setStatus(result.getStatus());
        unavailableVenue.setStatusDesc(getStatusDesc(result.getStatus()));
        
        return unavailableVenue;
    }
    
    /**
     * 场地可用性分析结果内部类
     */
    private static class VenueAvailabilityResult {
        private Venue venue;
        private boolean available;
        private String reason;
        private Integer status;
        
        // getters and setters
        public Venue getVenue() { return venue; }
        public void setVenue(Venue venue) { this.venue = venue; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
} 