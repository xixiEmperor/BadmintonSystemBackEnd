package com.wuli.badminton.dto;

import com.wuli.badminton.pojo.MallOrder;
import com.wuli.badminton.pojo.MallOrderItem;
import lombok.Data;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
public class OrderDto {
    private MallOrder order;
    private List<MallOrderItem> orderItems;
    private String statusDesc;
    
    public OrderDto(MallOrder order, List<MallOrderItem> orderItems) {
        this.order = order;
        this.orderItems = orderItems;
        this.statusDesc = order.getStatusDesc();
    }
} 