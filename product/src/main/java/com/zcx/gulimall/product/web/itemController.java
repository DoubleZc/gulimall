package com.zcx.gulimall.product.web;


import com.zcx.gulimall.product.service.SkuInfoService;
import com.zcx.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class itemController
{

	@Autowired
	SkuInfoService skuInfoService;

	@RequestMapping("/{skuId}.html")
	public String skuItem(@PathVariable Long skuId)
	{


		SkuItemVo skuItemvo = skuInfoService.item(skuId);


		return "item";
	}

/*
	SkuItemVo(
			info=SkuInfoEntity(skuId=35, spuId=4, skuName=HUAWEI nova 9亮黑色 8G+256G, skuDesc=null, catalogId=225, brandId=6, skuDefaultImg=https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//a7f3289a-4976-4e53-8967-9329c8fb8f26_28f296629cca865e.jpg, skuTitle=HUAWEI nova 9  亮黑色 8G+256G后置5000万超感知影像 搭载HarmonyOS 2, skuSubtitle=【华为nova9】120Hz原色臻彩环幕屏,后置5000万超感知影像,搭载HarmonyOS2,速来抢购;华为手机热销爆款，限量抢购，, price=2699.0000, saleCount=0),


			images=[
					SkuImagesEntity(id=43, skuId=35, imgUrl=https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//32fc5365-a24e-418d-871f-10289f58b2c0_a2c208410ae84d1f.jpg, imgSort=null, defaultImg=0),
					SkuImagesEntity(id=44, skuId=35, imgUrl=https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//a7f3289a-4976-4e53-8967-9329c8fb8f26_28f296629cca865e.jpg, imgSort=null, defaultImg=1),
	                SkuImagesEntity(id=45, skuId=35, imgUrl=https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//66f05f4a-4836-47ae-8bbf-120d9f0bba5c_b8494bf281991f94.jpg, imgSort=null, defaultImg=0)
	                ],


			desc=SpuInfoDescEntity(spuId=4, decript=https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//07576025-ab77-447b-8c16-3b8f6c93a191_73366cc235d68202.jpg,https://guli-zcx.oss-cn-nanjing.aliyuncs.com/2022-07-03//04c0838f-2b16-4e3f-8886-841de5ed7bd8_528211b97272d88a.jpg),


			skuSaleAttrs=[
				SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=亮黑色),
				SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+256G)],


			groupAttrs=[
				SkuItemVo.SpuItemBaseAttr(
					groupName=主体,
					attrs=[SkuItemVo.BaseAttrs(attrName=入网型号, attrValue=LIO-AL00), SkuItemVo.BaseAttrs(attrName=上市年份, attrValue=2019)]),
				SkuItemVo.SpuItemBaseAttr(
					groupName=主芯片,
					attrs=[SkuItemVo.BaseAttrs(attrName=CPU品牌, attrValue=海思（Hisilicon）), SkuItemVo.BaseAttrs(attrName=CPU型号, attrValue=HUAWEI Kirin 980)]),
				SkuItemVo.SpuItemBaseAttr(
					groupName=基本信息,
					attrs=[SkuItemVo.BaseAttrs(attrName=机身颜色, attrValue=黑色), SkuItemVo.BaseAttrs(attrName=机身长度（mm）, attrValue=158.3), SkuItemVo.BaseAttrs(attrName=机身材质工艺, attrValue=以官网信息为准;陶瓷)])],




			spuAttrs={
				35=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=亮黑色), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+256G)],
				36=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=亮黑色), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+128G)],
				37=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=普罗旺斯), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+256G)],
				38=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=普罗旺斯), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+128G)],
				39=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=绮境森林), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+256G)],
				40=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=绮境森林), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+128G)],
				41=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=9号色), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+256G)],
				42=[SkuItemVo.SkuSaleAttr(attrId=9, attrName=颜色, attrValue=9号色), SkuItemVo.SkuSaleAttr(attrId=12, attrName=版本, attrValue=8G+128G)]})



	*/


}
