package com.zcx.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.common.utils.CheckException;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.gulimall.member.entity.GithubUser;
import com.zcx.gulimall.member.fegin.CouponFeginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.gulimall.member.entity.MemberEntity;
import com.zcx.gulimall.member.service.MemberService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 会员
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:44:01
 */
@RestController
@RequestMapping("member/member")
public class MemberController
{
	@Autowired
	private MemberService memberService;


	@Autowired
	private CouponFeginService couponFeginService;

	

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//  @RequiresPermissions("member:member:list")
	public R list(@RequestParam Map<String, Object> params)
	{
		PageUtils page = memberService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("member:member:info")
	public R info(@PathVariable("id") Long id)
	{
		MemberEntity member = memberService.getById(id);

		return R.ok().put("member", member);
	}


	/**
	 * 保存
	 */
	@RequestMapping("/regist")
	//    @RequiresPermissions("member:member:save")
	public R save(@RequestBody MemberEntity member)
	{

		try {
			memberService.regist(member);
		} catch (CheckException e) {
			return R.error(e.getCode());
		}
		return R.ok();
	}


	/**
	 * 登录验证
	 */
	@RequestMapping("/login")
	public R login(@RequestBody MemberEntity member)
	{
		
		MemberEntity memberEntity = memberService.login(member);
		if (memberEntity != null) {
			return R.ok().put("data", memberEntity);
		} else {
			return R.error(ExceptionCode.L_PASSWORD_NOT);
		}
	}



	/**
	 * 第三方平台登录
	 */
	@RequestMapping("/auth/login")
	public R login(@RequestBody GithubUser githubUser)
	{

		MemberEntity memberEntity = memberService.login(githubUser);
		return R.ok().put("data", memberEntity);

	}




	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//  @RequiresPermissions("member:member:update")
	public R update(@RequestBody MemberEntity member)
	{
		memberService.updateById(member);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("member:member:delete")
	public R delete(@RequestBody Long[] ids)
	{
		memberService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}


}
