package com.zcx.gulimall.member.dao;

import com.zcx.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:44:01
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {


}
