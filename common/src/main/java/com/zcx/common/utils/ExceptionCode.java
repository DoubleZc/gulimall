package com.zcx.common.utils;


public enum ExceptionCode {
    VALID_EXCEPTION(10001,"参数校验失败"),
    UNKNOW_EXCEPTION(10000,"未知异常"),
    SQL_EXCEPTION(10002,"sql语句异常"),
    PRODUCT_UP_ERROR(50001,"商品上架失败"),
    SMS_CODE_EXCEPTION(20001,"验证码获取频率过快");


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
