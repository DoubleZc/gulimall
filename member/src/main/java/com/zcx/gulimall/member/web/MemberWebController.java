package com.zcx.gulimall.member.web;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zcx.common.utils.Constant;
import com.zcx.common.utils.R;
import com.zcx.gulimall.member.fegin.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController
{
	@Autowired
	OrderFeignService orderFeignService;
	
	

	@GetMapping("/memberOrder.html")
	public String order(@RequestParam(value = "pageNum",defaultValue = "1")String pageNum,
	                    @RequestParam(value = "limit",defaultValue = "10")String limit,
	                    Model model)
	{
		Map<String,Object>page=new HashMap<>();
		page.put(Constant.PAGE,pageNum);
		page.put(Constant.LIMIT,limit);
		R r = orderFeignService.listitem(page);
		model.addAttribute("orders",r.get("page"));
		
		return "orderlist";
	}
	
	
}
