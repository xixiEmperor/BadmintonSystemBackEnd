package com.wuli.badminton.vo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wuli.badminton.enums.ResponseEnum;
import lombok.Data;


/**
 * @JsonInclude注解确保JSON序列化时不包含值为null的字段
 * @param <T> 返回数据的类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResponseVo<T> {
    /**
     * 状态码：0表示成功，非0表示各种错误，具体含义见ResponseEnum
     */
    private Integer code;
    private String msg;
    private T data;

    private ResponseVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private ResponseVo(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
    
    /**
     * 创建成功响应，包含数据
     * @param data 返回的数据
     * @return 成功的响应对象
     */
    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), data);
    }
    
    /**
     * 创建成功响应，包含消息和数据
     * @param msg 成功消息
     * @param data 返回的数据
     * @return 成功的响应对象
     */
    public static <T> ResponseVo<T> success(String msg, T data) {
        ResponseVo<T> response = new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), data);
        response.setMsg(msg);
        return response;
    }
    
    /**
     * 创建成功响应，只包含成功状态，不包含数据
     * @return 成功的响应对象
     */
    public static <T> ResponseVo<T> success() {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getDesc());
    }
    
    /**
     * 创建错误响应，使用预定义的错误枚举当错误信息已经在 ResponseEnum 中定义好时（如"商品不存在"、"密码错误"），可以直接传入枚举对象。
     * @param responseEnum 错误枚举值
     * @return 错误的响应对象
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum) {
        return new ResponseVo<>(responseEnum.getCode(), responseEnum.getDesc());
    }

    /**
     * 创建错误响应，使用预定义的错误枚举，但自定义错误消息
     * @param responseEnum 错误枚举值
     * @param msg 自定义错误消息
     * @return 错误的响应对象
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg) {
        return new ResponseVo<>(responseEnum.getCode(), msg);
    }
    
    /**
     * 创建错误响应，使用自定义错误码和消息
     * @param code 错误码
     * @param msg 错误消息
     * @return 错误的响应对象
     */
    public static <T> ResponseVo<T> error(Integer code, String msg) {
        return new ResponseVo<>(code, msg);
    }
    
    /**
     * 参数错误响应
     * @param errors 错误信息列表
     * @return 响应对象
     */
    public static <T> ResponseVo<T> paramError(T errors) {
        ResponseVo<T> response = new ResponseVo<>(ResponseEnum.PARAM_ERROR.getCode(), errors);
        response.setMsg(ResponseEnum.PARAM_ERROR.getDesc());
        return response;
    }
}