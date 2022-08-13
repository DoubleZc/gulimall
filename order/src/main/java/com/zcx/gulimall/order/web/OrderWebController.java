package com.zcx.gulimall.order.web;


import com.alipay.api.AlipayApiException;
import com.zcx.common.comm.CosException;
import com.zcx.common.utils.R;
import com.zcx.gulimall.order.config.AlipayTemplate;
import com.zcx.gulimall.order.service.OrderService;
import com.zcx.gulimall.order.vo.OrderConfirmVo;
import com.zcx.gulimall.order.vo.OrderSubmitVo;
import com.zcx.gulimall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
public class OrderWebController
{
	@Autowired
	OrderService orderService;
	
	@Autowired
	AlipayTemplate alipayTemplate;
	
	
	@GetMapping("/toTrade")
	public String toConfirm(Model model)
	{
		OrderConfirmVo vo=orderService.confirm();
		
		model.addAttribute("orderConfirmData", vo);
		return "confirm";
		
	}
	
	@PostMapping("/submit")
	public String submitOrder( OrderSubmitVo vo ,Model model){
		try {
			R res = orderService.submitOrder(vo);
			if (res.getCode()==0)
			{
				model.addAttribute("order",res);
				//成功
				return "pay";
			}
			else
			{
				log.warn(res.get("msg").toString());
				//失败
				//1.价格不一致
				//2.重复提交
				return "redirect:http://order.gulimall.com/toTrade";
				
			}
		} catch (CosException.NotStock notStock) {
			notStock.printStackTrace();
			//没库存
			log.warn("商品{}，{}",notStock.getSkuIds(),notStock.getCode().getMsg());
			return "redirect:http://order.gulimall.com/toTrade";
		}
		
	}
	
	@GetMapping(value = "/payOrder",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String payOrder(@RequestParam  String orderSn) throws AlipayApiException
	{
		PayVo payVo=orderService.getOrderPay(orderSn);
		return alipayTemplate.pay(payVo);
		
	}
	
	
	
	
}
