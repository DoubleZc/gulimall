package com.zcx.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.utils.*;
import com.zcx.gulimall.member.entity.GithubUser;
import com.zcx.gulimall.member.entity.MemberLevelEntity;
import com.zcx.gulimall.member.service.MemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zcx.gulimall.member.dao.MemberDao;
import com.zcx.gulimall.member.entity.MemberEntity;
import com.zcx.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Autowired
	MemberLevelService memberLevelService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void regist(MemberEntity member) throws CheckException
	{

		MemberLevelEntity defaultLevel = memberLevelService.getDefaultLevel();
		member.setLevelId(defaultLevel.getId());

		checkPhoneUnique(member.getMobile());
		checkUsernameUnique(member.getUsername());

		member.setNickname(member.getUsername());

		String password="";
		try {
			 password = EncryptUtil.getPassword(member.getPassword());
		} catch (Exception e) {
			throw new CheckException(ExceptionCode.R_PASSWORD_NOT_ALLOW);
		}
		member.setPassword(password);
		save(member);


	}


	@Override
	public void checkPhoneUnique(String mobile) throws CheckException
	{
		MemberDao memberDao= this.baseMapper;
		Long count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", mobile));
		 count += memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", mobile));
		if (count>0)
		{
			throw new CheckException(ExceptionCode.R_PHONE_NOT_UNIQUE);
		}

	}

	@Override
	public void checkUsernameUnique(String username) throws CheckException
	{

		MemberDao memberDao = this.baseMapper;
		Long count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
		 count += memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", username));
		if (count>0)
		{
			throw new CheckException(ExceptionCode.R_USERNAME_NOT_UNIQUE);
		}

	}

	@Override
	public MemberEntity login(MemberEntity member)
	{

		MemberEntity entity = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, member.getUsername())
				.or().eq(MemberEntity::getMobile, member.getUsername()));
		if (entity!=null
		&&EncryptUtil.validatePassword(member.getPassword(),entity.getPassword()))
		{
			return  entity;
		}

		return null;
	}

	@Override
	public MemberEntity login(GithubUser githubUser)
	{
		//登录和注册
		long id = githubUser.getId();
		MemberEntity memberEntity = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getGithub, id));
		if (memberEntity!=null)
		{
			//有用户绑定
			return memberEntity;

		}else
		{
			MemberEntity memberEntity1 = new MemberEntity();
			memberEntity1.setNickname(githubUser.getLogin());
			MemberLevelEntity defaultLevel = memberLevelService.getDefaultLevel();
			memberEntity1.setLevelId(defaultLevel.getId());
			memberEntity1.setGithub(id);
			save(memberEntity1);

			return memberEntity;
		}

	}
}