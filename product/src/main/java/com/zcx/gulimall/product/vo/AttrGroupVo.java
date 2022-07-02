package com.zcx.gulimall.product.vo;

import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AttrGroupVo extends AttrGroupEntity
{

	@Getter
	@Setter
	private List<AttrEntity> attrs;

}
