package com.zcx.gulimall.product.app;

import java.util.Arrays;
import java.util.List;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.product.entity.CategoryEntity;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.common.utils.R;



/**
 * 商品三级分类
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:41:22
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
  //  @RequiresPermissions("product:category:list")
    public R list(){

        List<CategoryEntity> entities= categoryService.listWithTree();
        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetail(category);
        return R.ok();
    }

    @RequestMapping("/update/list")
    public R updateList(@RequestBody List<CategoryEntity> entities)
    {
        categoryService.updateBatchById(entities);
        return R.ok();

    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
//        检查菜单是否被引用
        categoryService.removeMenu(Arrays.asList(catIds));
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete/list")
    //@RequiresPermissions("product:category:delete")
    public R deleteList(@RequestBody List<CategoryEntity> entities){
//        检查菜单是否被引用
        categoryService.removeBatchByIds(entities);
        return R.ok();
    }
}
