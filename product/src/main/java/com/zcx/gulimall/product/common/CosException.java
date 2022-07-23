package com.zcx.gulimall.product.common;

import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.zcx.gulimall.product.app")
@Component
public class CosException {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R handleValidException(BindException exception) {
        BindingResult result = exception.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach(item -> {
            String defaultMessage = item.getDefaultMessage();
            String field = item.getField();
            map.put(field, defaultMessage);
        });

        return R.error(ExceptionCode.VALID_EXCEPTION).put("data", map);
    }


    @ExceptionHandler(BadSqlGrammarException.class)
    public R handleException(BadSqlGrammarException e)
    {
        String msg="";
        String[] split = e.getMessage().split("###");
        for (String s : split) {
            if (s.trim().startsWith("SQL:"))
            {
                msg=s;
                break;
            }
        }
        return R.error(ExceptionCode.SQL_EXCEPTION).put("data",msg);

    }


}



