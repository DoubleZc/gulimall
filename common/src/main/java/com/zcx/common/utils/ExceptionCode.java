package com.zcx.common.utils;

public enum ExceptionCode {
    VALID_EXCEPTION(10001,"参数校验失败"),
    UNKNOW_EXCEPTION(10000,"未知异常"),
    SQL_EXCEPTION(10002,"sql语句异常"),
    PRODUCT_UP_ERROR(50001,"商品上架失败");


    private final Integer code;
    private final String message;

    ExceptionCode(int code ,String msg)
    {
        this.code=code;
        this.message=msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
