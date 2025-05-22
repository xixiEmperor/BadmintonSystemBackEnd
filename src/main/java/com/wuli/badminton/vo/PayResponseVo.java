package com.wuli.badminton.vo;

import lombok.Data;

/**
 * 支付响应VO
 */
@Data
public class PayResponseVo {
    
    /**
     * 响应码：0-成功，非0-失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 支付链接或二维码地址
     */
    private String payUrl;
    
    /**
     * 私有构造函数
     */
    private PayResponseVo() {}
    
    /**
     * 创建成功响应
     * @param payUrl 支付URL
     * @return 响应对象
     */
    public static PayResponseVo success(String payUrl) {
        PayResponseVo vo = new PayResponseVo();
        vo.setCode(0);
        vo.setMessage("支付创建成功");
        vo.setPayUrl(payUrl);
        return vo;
    }
    
    /**
     * 创建失败响应
     * @param errorMsg 错误信息
     * @return 响应对象
     */
    public static PayResponseVo error(String errorMsg) {
        PayResponseVo vo = new PayResponseVo();
        vo.setCode(-1);
        vo.setMessage(errorMsg);
        return vo;
    }
} 