package com.zcx.gulimall;

import com.alibaba.fastjson.JSON;
import com.zcx.common.utils.EncryptUtil;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.JWTUntils;
import com.zcx.gulimall.vo.UserLoginVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = AuthServerApplication.class)
class AuthServerApplicationTests
{

	@Test
	void contextLoads()
	{




		UserLoginVo userLoginVo1 = new UserLoginVo();
		userLoginVo1.setUsername("zhangsan");
		userLoginVo1.setPassword("da");


		String token = JWTUntils.getToken(userLoginVo1);
		System.out.println(token);
		UserLoginVo userLoginVo = JWTUntils.ResolveToken(token, UserLoginVo.class);
		System.out.println(userLoginVo);

	}

}
