package com.zcx.gulimall.search.web;


import com.zcx.gulimall.search.service.MallSearchService;
import com.zcx.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController
{
	@Autowired
	MallSearchService mallSearchService;


	@GetMapping("/list.html")
	public String index(SearchParam param)
	{
		Object o= mallSearchService.search(param);
		return "list";
	}









}
