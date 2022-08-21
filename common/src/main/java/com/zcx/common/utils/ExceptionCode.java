package com.zcx.common.utils;


public enum ExceptionCode {
    // 通用错误
    VALID_EXCEPTION(10001,"参数校验失败"),
    UNKNOW_EXCEPTION(10000,"未知异常"),
    SQL_EXCEPTION(10002,"sql语句异常"),
    //远程调用异常
    FEIGN_EXCEPTION(10005,"服务器正忙，请稍后重试"),


    //商品错误
    PRODUCT_UP_ERROR(50001,"商品上架失败"),


    //注册错误
    SMS_CODE_EXCEPTION(20001,"验证码获取频率过快"),
    R_USERNAME_NOT_UNIQUE(20002,"用户名已存在"),
    R_PHONE_NOT_UNIQUE(20003,"手机号已使用"),
    R_PASSWORD_NOT_ALLOW(20004,"密码格式错误"),
    //登录错误信息
    L_PASSWORD_NOT(20010,"账号或密码错误"),

    //订单错误
    ORDER_SUBMIT_ERROR(30001,"订单验证失败，稍后重试"),
    //货存不足
    ORDER_NOT_STOCK(30002,"货存不足"),
    ORDER_DIFFERENT_PRICE(30003,"两次价格不一致"),
    
    
    
    
    //消息队列错误
    CLASS_ERROR(40001,"类型转换异常，类型不一致"),
    MQ_ERROR(40002,"消息发送异常"),
    
    
    
    //秒杀错误
    K_TIME_ERROR(50001,"该商品不在秒杀时间"),
    K_CODE_ERROR(50002,"验证码不一致"),
    

    //feign的失败回调
    TOO_MANY_REQUEST(60001,"请求次数过多"),
    
    


    ;
    private final Integer code;
    private final String msg;

    ExceptionCode(int code ,String msg)
    {
        this.code=code;
        this.msg=msg;
    }


    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
