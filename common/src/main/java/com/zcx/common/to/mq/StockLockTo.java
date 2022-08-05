package com.zcx.common.to.mq;


import lombok.Data;

import java.util.List;

@Data
public class StockLockTo
{
	
	private Long id;
	private List<Long> detailIds;
	
	
}
