package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.ReservationOrderMapper;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.dao.VenueMapper;
import com.wuli.badminton.dto.ReservationOrderDto;
import com.wuli.badminton.dto.ReservationOrderQueryDto;
import com.wuli.badminton.dto.VenueAvailabilityDto;
import com.wuli.badminton.enums.ReservationStatusEnum;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.enums.VenueStatusEnum;
import com.wuli.badminton.pojo.ReservationOrder;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.pojo.Venue;
import com.wuli.badminton.service.ReservationOrderService;
import com.wuli.badminton.vo.ReservationOrderVo;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预约订单Service实现类
 */
@Service
@Slf4j
public class ReservationOrderServiceImpl implements ReservationOrderService {
    
    @Autowired
    private ReservationOrderMapper reservationOrderMapper;
    
    @Autowired
    private VenueMapper venueMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    @Transactional
    public ResponseVo<ReservationOrderVo> createOrder(Integer userId, ReservationOrderDto dto) {
        try {
            // 1. 验证用户是否存在
            User user = userMapper.findById(userId.longValue());
            if (user == null) {
                return ResponseVo.error(ResponseEnum.EMAIL_NOT_EXIST); // 暂时使用现有错误码
            }
            
            // 2. 验证场地是否存在且可用
            Venue venue = venueMapper.selectById(dto.getVenueId());
            if (venue == null) {
                return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
            }
            if (!VenueStatusEnum.AVAILABLE.getCode().equals(venue.getStatus())) {
                return ResponseVo.error(ResponseEnum.VENUE_UNDER_MAINTENANCE);
            }
            
            // 3. 验证预约时间
            Date reservationDate = dateFormat.parse(dto.getReservationDate());
            if (reservationDate.before(new Date())) {
                return ResponseVo.error(ResponseEnum.INVALID_RESERVATION_TIME);
            }
            
            // 4. 验证时间段格式并计算时长
            LocalTime startTime = LocalTime.parse(dto.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(dto.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));
            
            if (!endTime.isAfter(startTime)) {
                return ResponseVo.error(ResponseEnum.PARAM_ERROR);
            }
            
            // 计算时长（以小时为单位，支持小数）
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            BigDecimal duration = new BigDecimal(minutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
            
            // 5. 检查时间段冲突
            List<ReservationOrder> conflicts = reservationOrderMapper.selectConflictOrders(
                    dto.getVenueId(), reservationDate, dto.getStartTime(), dto.getEndTime());
            if (!conflicts.isEmpty()) {
                return ResponseVo.error(ResponseEnum.RESERVATION_TIME_CONFLICT);
            }
            
            // 6. 创建订单
            ReservationOrder order = new ReservationOrder();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setUsername(user.getUsername());
            order.setVenueId(dto.getVenueId());
            order.setVenueName(venue.getName());
            order.setReservationDate(reservationDate);
            order.setStartTime(dto.getStartTime());
            order.setEndTime(dto.getEndTime());
            order.setDuration(duration.intValue());
            order.setPricePerHour(venue.getPricePerHour());
            order.setTotalAmount(venue.getPricePerHour().multiply(duration));
            order.setStatus(ReservationStatusEnum.PENDING_PAYMENT.getCode());
            order.setPayType(dto.getPayType());
            order.setRemark(dto.getRemark());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            
            int result = reservationOrderMapper.insert(order);
            if (result > 0) {
                return ResponseVo.success(convertToVo(order));
            } else {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
            
        } catch (ParseException e) {
            log.error("日期解析错误", e);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseVo.error(ResponseEnum.ERROR);
        }
    }
    
    @Override
    public ResponseVo<ReservationOrderVo> getOrderById(Long id) {
        ReservationOrder order = reservationOrderMapper.selectById(id);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        return ResponseVo.success(convertToVo(order));
    }
    
    @Override
    public ResponseVo<ReservationOrderVo> getOrderByNo(String orderNo) {
        ReservationOrder order = reservationOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        return ResponseVo.success(convertToVo(order));
    }
    
    @Override
    public ResponseVo<Map<String, Object>> getOrderList(ReservationOrderQueryDto queryDto) {
        // 计算分页参数
        int offset = (queryDto.getPage() - 1) * queryDto.getSize();
        queryDto.setPage(offset);
        
        List<ReservationOrder> orders = reservationOrderMapper.selectByCondition(queryDto);
        int total = reservationOrderMapper.countByCondition(queryDto);
        
        List<ReservationOrderVo> orderVos = orders.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderVos);
        result.put("total", total);
        result.put("page", queryDto.getPage() / queryDto.getSize() + 1);
        result.put("size", queryDto.getSize());
        
        return ResponseVo.success(result);
    }
    
    @Override
    public ResponseVo<List<ReservationOrderVo>> getUserOrders(Integer userId, Integer status) {
        List<ReservationOrder> orders = reservationOrderMapper.selectByUserId(userId, status);
        List<ReservationOrderVo> orderVos = orders.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        return ResponseVo.success(orderVos);
    }
    
    @Override
    @Transactional
    public ResponseVo<String> cancelOrder(Integer userId, Long orderId, String reason) {
        ReservationOrder order = reservationOrderMapper.selectById(orderId);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        
        // 验证订单所有者
        if (!order.getUserId().equals(userId)) {
            return ResponseVo.error(ResponseEnum.UNAUTHORIZED);
        }
        
        // 只有待支付状态的订单可以取消
        if (!ReservationStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ResponseEnum.ORDER_PAID);
        }
        
        order.setStatus(ReservationStatusEnum.CANCELLED.getCode());
        order.setCancelReason(reason);
        order.setUpdateTime(new Date());
        
        int result = reservationOrderMapper.updateById(order);
        if (result > 0) {
            return ResponseVo.success("订单取消成功");
        } else {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> refundOrder(Long orderId, String reason) {
        ReservationOrder order = reservationOrderMapper.selectById(orderId);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        
        // 只有已支付状态的订单可以退款
        if (!ReservationStatusEnum.PAID.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED);
        }
        
        // 检查是否在可退款时间内（预约开始时间前30分钟）
        try {
            // 解析预约日期和开始时间
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String reservationDateTimeStr = dateFormat.format(order.getReservationDate()) + " " + order.getStartTime();
            Date reservationDateTime = dateTimeFormat.parse(reservationDateTimeStr);
            
            // 计算退款截止时间（预约开始前30分钟）
            Calendar cal = Calendar.getInstance();
            cal.setTime(reservationDateTime);
            cal.add(Calendar.MINUTE, -30); // 预约开始时间前30分钟
            Date refundDeadline = cal.getTime();
            
            Date now = new Date();
            if (now.after(refundDeadline)) {
                return ResponseVo.error(ResponseEnum.REFUND_TIME_LIMIT, "距离预约开始不足30分钟，无法申请退款");
            }
            
            log.info("退款时间检查通过：当前时间={}, 退款截止时间={}, 预约时间={}", 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(refundDeadline),
                    reservationDateTimeStr);
            
        } catch (Exception e) {
            log.error("计算退款时间失败", e);
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        
        // 更新订单状态为退款中
        order.setStatus(ReservationStatusEnum.REFUNDING.getCode());
        order.setCancelReason(reason);
        order.setUpdateTime(new Date());
        
        int result = reservationOrderMapper.updateById(order);
        if (result > 0) {
            log.info("用户申请退款成功，订单ID: {}, 原因: {}", orderId, reason);
            return ResponseVo.success("退款申请提交成功，请等待管理员审核");
        } else {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> paymentCallback(String orderNo, Long payInfoId) {
        ReservationOrder order = reservationOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        
        if (!ReservationStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ResponseEnum.ORDER_PAID);
        }
        
        int result = reservationOrderMapper.updatePayInfo(order.getId(), payInfoId, order.getPayType(), new Date());
        if (result > 0) {
            reservationOrderMapper.updateStatus(order.getId(), ReservationStatusEnum.PAID.getCode());
            return ResponseVo.success("支付成功");
        } else {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
    }
    
    @Override
    public ResponseVo<Map<String, Object>> checkVenueAvailability(VenueAvailabilityDto dto) {
        try {
            Date queryDate = dateFormat.parse(dto.getDate());
            
            // 查询指定日期的所有预约记录
            List<ReservationOrder> reservations;
            if (dto.getVenueId() != null) {
                reservations = reservationOrderMapper.selectByVenueAndDate(dto.getVenueId(), queryDate);
            } else {
                // 查询所有场地的预约记录
                ReservationOrderQueryDto queryDto = new ReservationOrderQueryDto();
                queryDto.setStartDate(dto.getDate());
                queryDto.setEndDate(dto.getDate());
                reservations = reservationOrderMapper.selectByCondition(queryDto);
            }
            
            Map<String, Object> result = new HashMap<>();
            
            if (dto.getStartTime() != null && dto.getEndTime() != null) {
                // 检查特定时段是否可用
                boolean isAvailable = true;
                for (ReservationOrder reservation : reservations) {
                    if (dto.getVenueId() != null && !reservation.getVenueId().equals(dto.getVenueId())) {
                        continue;
                    }
                    
                    // 检查时间冲突
                    if (!isTimeSlotAvailable(dto.getStartTime(), dto.getEndTime(), 
                            reservation.getStartTime(), reservation.getEndTime())) {
                        isAvailable = false;
                        break;
                    }
                }
                result.put("isAvailable", isAvailable);
            }
            
            // 返回所有预约记录
            List<ReservationOrderVo> reservationVos = reservations.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
            result.put("reservations", reservationVos);
            
            return ResponseVo.success(result);
            
        } catch (ParseException e) {
            log.error("日期解析错误", e);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR);
        }
    }
    
    @Override
    public ResponseVo<List<ReservationOrderVo>> getVenueReservations(Integer venueId, String date) {
        try {
            Date queryDate = dateFormat.parse(date);
            List<ReservationOrder> orders = reservationOrderMapper.selectByVenueAndDate(venueId, queryDate);
            List<ReservationOrderVo> orderVos = orders.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
            return ResponseVo.success(orderVos);
        } catch (ParseException e) {
            log.error("日期解析错误", e);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR);
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> completeOrder(Long orderId) {
        ReservationOrder order = reservationOrderMapper.selectById(orderId);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        
        if (!ReservationStatusEnum.PAID.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED);
        }
        
        int result = reservationOrderMapper.updateStatus(orderId, ReservationStatusEnum.COMPLETED.getCode());
        if (result > 0) {
            return ResponseVo.success("订单完成");
        } else {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
    }
    
    @Override
    public ResponseVo<Map<String, Object>> getAdminOrderList(ReservationOrderQueryDto queryDto) {
        return getOrderList(queryDto);
    }
    
    @Override
    @Transactional
    public ResponseVo<String> approveRefund(Long orderId, Boolean approved, String adminRemark) {
        ReservationOrder order = reservationOrderMapper.selectById(orderId);
        if (order == null) {
            return ResponseVo.error(ResponseEnum.RESERVATION_NOT_EXIST);
        }
        
        // 只有退款中状态的订单可以审批
        if (!ReservationStatusEnum.REFUNDING.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED, "订单状态不正确，无法审批");
        }
        
        if (approved) {
            // 审批通过，完成退款
            order.setStatus(ReservationStatusEnum.CLOSED.getCode());
            order.setRefundAmount(order.getTotalAmount());
            order.setRefundTime(new Date());
            if (adminRemark != null && !adminRemark.trim().isEmpty()) {
                order.setCancelReason(order.getCancelReason() + " [管理员备注: " + adminRemark + "]");
            }
            order.setUpdateTime(new Date());
            
            int result = reservationOrderMapper.updateById(order);
            if (result > 0) {
                log.info("管理员审批退款通过，订单ID: {}, 退款金额: {}", orderId, order.getTotalAmount());
                // TODO: 调用实际退款接口
                return ResponseVo.success("退款审批通过，已完成退款");
            } else {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        } else {
            // 审批拒绝，恢复已支付状态
            order.setStatus(ReservationStatusEnum.PAID.getCode());
            if (adminRemark != null && !adminRemark.trim().isEmpty()) {
                order.setCancelReason("退款被拒绝: " + adminRemark);
            } else {
                order.setCancelReason("退款被管理员拒绝");
            }
            order.setUpdateTime(new Date());
            
            int result = reservationOrderMapper.updateById(order);
            if (result > 0) {
                log.info("管理员拒绝退款申请，订单ID: {}, 原因: {}", orderId, adminRemark);
                return ResponseVo.success("退款申请已拒绝");
            } else {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "RO" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    /**
     * 检查时间段是否可用
     */
    private boolean isTimeSlotAvailable(String newStart, String newEnd, String existStart, String existEnd) {
        LocalTime newStartTime = LocalTime.parse(newStart);
        LocalTime newEndTime = LocalTime.parse(newEnd);
        LocalTime existStartTime = LocalTime.parse(existStart);
        LocalTime existEndTime = LocalTime.parse(existEnd);
        
        // 如果新时段的结束时间早于或等于已有时段的开始时间，或者新时段的开始时间晚于或等于已有时段的结束时间，则不冲突
        return newEndTime.compareTo(existStartTime) <= 0 || newStartTime.compareTo(existEndTime) >= 0;
    }
    
    /**
     * 转换为VO对象
     */
    private ReservationOrderVo convertToVo(ReservationOrder order) {
        ReservationOrderVo vo = new ReservationOrderVo();
        BeanUtils.copyProperties(order, vo);
        
        // 格式化日期时间
        vo.setReservationDate(dateFormat.format(order.getReservationDate()));
        vo.setTimeSlot(order.getStartTime() + "-" + order.getEndTime());
        
        if (order.getPayTime() != null) {
            vo.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getPayTime()));
        }
        if (order.getRefundTime() != null) {
            vo.setRefundTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getRefundTime()));
        }
        if (order.getCreateTime() != null) {
            vo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime()));
        }
        if (order.getUpdateTime() != null) {
            vo.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getUpdateTime()));
        }
        
        // 设置状态描述
        ReservationStatusEnum statusEnum = ReservationStatusEnum.getByCode(order.getStatus());
        if (statusEnum != null) {
            vo.setStatusDesc(statusEnum.getDesc());
        }
        
        // 设置支付方式描述
        if (order.getPayType() != null) {
            vo.setPayTypeDesc(order.getPayType() == 1 ? "支付宝" : "微信");
        }
        
        // 设置操作权限
        vo.setCanCancel(ReservationStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus()));
        vo.setCanRefund(ReservationStatusEnum.PAID.getCode().equals(order.getStatus()) && 
                       isWithinRefundTime(order.getReservationDate(), order.getStartTime()));
        
        return vo;
    }
    
    /**
     * 检查是否在退款时间内（预约开始前30分钟）
     */
    private boolean isWithinRefundTime(Date reservationDate, String startTime) {
        try {
            // 解析预约日期和开始时间
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String reservationDateTimeStr = dateFormat.format(reservationDate) + " " + startTime;
            Date reservationDateTime = dateTimeFormat.parse(reservationDateTimeStr);
            
            // 计算退款截止时间（预约开始前30分钟）
            Calendar cal = Calendar.getInstance();
            cal.setTime(reservationDateTime);
            cal.add(Calendar.MINUTE, -30);
            Date refundDeadline = cal.getTime();
            
            return new Date().before(refundDeadline);
        } catch (Exception e) {
            log.error("计算退款时间失败", e);
            return false;
        }
    }
} 