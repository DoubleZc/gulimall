package com.zcx.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.common.valid.AddGroup;
import com.zcx.common.valid.UpdateGroup;
import com.zcx.common.valid.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.product.entity.BrandEntity;
import com.zcx.gulimall.product.service.BrandService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 品牌
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:41:22
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
  //  @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.findPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
            brandService.save(brand);
            return R.ok();




    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class})@RequestBody BrandEntity brand){
		brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    //  @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatus.class})@RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }



}
