package com.zcx.gulimall.coupon.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.gulimall.coupon.entity.CouponEntity;
import com.zcx.gulimall.coupon.service.CouponService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 优惠券信息
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:32:50
 */

@Data
@ConfigurationProperties(prefix = "userd")
@RestController
@RequestMapping("coupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    private String name;
    private int age;



    @RequestMapping("test")
    public R test(){

        return R.ok().put("name",name).put("age",age);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //  @RequiresPermissions("coupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    @RequestMapping("/member/list")
    public R memeberCoupon() {
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("满减");
        return R.ok().put("coupons", Arrays.asList(couponEntity));


    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:coupon:info")
    public R info(@PathVariable("id") Long id) {
        CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("coupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon) {
        couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //  @RequiresPermissions("coupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon) {
        couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:coupon:delete")
    public R delete(@RequestBody Long[] ids) {
        couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
