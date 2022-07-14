package com.zcx.gulimall;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class AspectTest
{

	@Around("execution(* *.*.*(..))")
	public  void test(ProceedingJoinPoint point) throws NoSuchMethodException
	{

	}


}
