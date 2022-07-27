package com.zcx.gulimall.search.web;


import com.zcx.common.utils.JWTUntils;
import com.zcx.gulimall.search.service.MallSearchService;
import com.zcx.gulimall.search.vo.SearchParam;
import com.zcx.gulimall.search.vo.SearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class SearchController
{
	@Autowired
	MallSearchService mallSearchService;


	/**
	 *
	 *
	 * @param param
	 * @return
	 */
	@GetMapping("/list.html")
	public String index(SearchParam param, Model model, HttpServletRequest request)
	{
		
		
		
		String queryString = request.getQueryString();
		param.set_url(queryString);

		//
		SearchRes o= mallSearchService.search(param);
		model.addAttribute("search",o);
		return "list";
	}


 






}
