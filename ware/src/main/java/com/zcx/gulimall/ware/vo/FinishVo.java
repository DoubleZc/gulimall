package com.zcx.gulimall.ware.vo;

import lombok.Data;

import java.util.List;


@Data
public class FinishVo
{
	Long id;
	List<FinishDetailVo> items;
}

