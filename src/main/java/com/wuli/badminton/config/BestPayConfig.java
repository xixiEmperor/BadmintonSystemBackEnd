package com.wuli.badminton.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 支付配置类
 */
@Component
public class BestPayConfig {
    
    @Value("${pay.wx.app-id}")
    private String wxAppId;
    
    @Value("${pay.wx.mch-id}")
    private String wxMchId;
    
    @Value("${pay.wx.mch-key}")
    private String wxMchKey;
    
    @Value("${pay.wx.notify-url}")
    private String wxNotifyUrl;
    
    @Value("${pay.wx.return-url}")
    private String wxReturnUrl;
    
    @Value("${pay.alipay.app-id}")
    private String alipayAppId;
    
    @Value("${pay.alipay.private-key}")
    private String alipayPrivateKey;
    
    @Value("${pay.alipay.public-key}")
    private String alipayPublicKey;
    
    @Value("${pay.alipay.notify-url}")
    private String alipayNotifyUrl;
    
    @Value("${pay.alipay.return-url}")
    private String alipayReturnUrl;
    
    @Bean
    public BestPayService bestPayService() {
        // 微信支付配置
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxAppId);
        wxPayConfig.setMchId(wxMchId);
        wxPayConfig.setMchKey(wxMchKey);
        wxPayConfig.setNotifyUrl(wxNotifyUrl);
        wxPayConfig.setReturnUrl(wxReturnUrl);
        
        // 支付宝配置
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(alipayAppId);
        aliPayConfig.setPrivateKey(alipayPrivateKey);
        aliPayConfig.setAliPayPublicKey(alipayPublicKey);
        aliPayConfig.setNotifyUrl(alipayNotifyUrl);
        aliPayConfig.setReturnUrl(alipayReturnUrl);
        
        // 初始化支付服务
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        
        return bestPayService;
    }
} 