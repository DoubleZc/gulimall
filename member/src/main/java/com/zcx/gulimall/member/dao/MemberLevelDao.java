package com.zcx.gulimall.member.dao;

import com.zcx.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:44:01
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
	MemberLevelEntity selectDafault();
}
