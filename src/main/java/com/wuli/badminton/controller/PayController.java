package com.wuli.badminton.controller;

import com.lly835.bestpay.model.PayResponse;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.pojo.PayInfo;
import com.wuli.badminton.service.PayService;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wuli.badminton.dto.CreatePayRequestDto;

/**
 * 支付相关Controller
 */
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {
    
    @Autowired
    private PayService payService;
    
    /**
     * 创建支付
     * @return 支付响应
     */
    @PostMapping("/create")
    public ResponseVo<PayResponse> create(@RequestBody CreatePayRequestDto requestDto) {
        PayResponse response = payService.createPay(requestDto.getOrderNo(), requestDto.getAmount(), requestDto.getBusinessType());
        return ResponseVo.success(response);
    }
    
    /**
     * 处理微信支付异步通知
     * @param notifyData 通知数据
     * @return 处理结果
     */
    @PostMapping("/notify")
    public String asyncNotify(@RequestBody String notifyData) {
        return payService.asyncNotify(notifyData);
    }
    
    /**
     * 查询支付状态
     * @param orderNo 订单号（支持数字和字符串格式）
     * @return 支付信息
     */
    @GetMapping("/query")
    public ResponseVo<PayInfo> queryPayStatus(@RequestParam("orderNo") String orderNo) {
        PayInfo payInfo = payService.queryByOrderId(orderNo);
        if (payInfo == null) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        return ResponseVo.success(payInfo);
    }
    
    /**
     * 获取支付成功后的跳转地址
     * @param orderNo 订单号（支持数字和字符串格式）
     * @return 跳转地址
     */
    @GetMapping("/return_url")
    public ResponseVo<String> getReturnUrl(@RequestParam("orderNo") String orderNo) {
        String returnUrl = payService.getReturnUrl(orderNo);
        return ResponseVo.success(returnUrl);
    }
} 