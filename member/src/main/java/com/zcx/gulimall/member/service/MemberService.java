package com.zcx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.CheckException;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.member.entity.GithubUser;
import com.zcx.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:44:01
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
	void regist(MemberEntity member) throws CheckException;
	void checkPhoneUnique(String mobile) throws CheckException;
	void checkUsernameUnique(String username) throws CheckException;
	MemberEntity login(MemberEntity member);
	MemberEntity login(GithubUser githubUser);
}

