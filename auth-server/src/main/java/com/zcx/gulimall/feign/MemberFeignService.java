package com.zcx.gulimall.feign;


import com.zcx.common.utils.R;
import com.zcx.gulimall.vo.UserLoginVo;
import com.zcx.gulimall.vo.UserRegistVo;
import com.zcx.gulimall.vo.github.GithubUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService
{

	@RequestMapping("/member/member/regist")
	 R save(@RequestBody UserRegistVo member);

	@RequestMapping("/member/member/login")
	 R login(@RequestBody UserLoginVo member);

	@RequestMapping("/member/member/auth/login")
	 R login(@RequestBody GithubUser githubUser);

}
