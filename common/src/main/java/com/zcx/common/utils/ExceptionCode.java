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
    L_PASSWORD_NOT(20010,"账号或密码错误")





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
