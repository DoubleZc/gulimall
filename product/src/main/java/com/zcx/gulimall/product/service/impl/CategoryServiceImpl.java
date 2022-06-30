package com.zcx.gulimall.product.service.impl;

import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.CategoryDao;
import com.zcx.gulimall.product.entity.CategoryEntity;
import com.zcx.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Override
    public Long[] findPath(Long catelogId) {

        List<Long>path=new ArrayList<>();
        path.add(catelogId);
        List<Long> init = getPath(catelogId, path);
        Collections.reverse(init);
        return  init.toArray(new Long[0]);


    }


    public  List<Long> getPath(Long catelogId,List<Long> path)
    {
        CategoryEntity entity = getById(catelogId);
        if (entity.getParentCid()!=0)
        {

            path.add(entity.getParentCid());
            getPath(entity.getParentCid(),path);

        }
        return path;
    }


    //删除数组
    @Override
    public void removeMenu(List<Long> asList) {

//TODO        检查菜单是否被引用
        removeByIds(asList);


    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntityList = list();
        List<CategoryEntity> collect0 = getChildren(categoryEntityList, 0L);
        return collect0;
    }

    /****
     * 查找子类方法
     * @param categoryEntityList 所有参数
     * @param selfId 自身
     * @return
     */

    private List<CategoryEntity> getChildren(List<CategoryEntity> categoryEntityList, Long selfId) {
        return categoryEntityList.stream().
                //找到所有子类
                        filter(item -> item.getParentCid() == selfId)
                //用递归将子类的children附上值
                .map(item -> {
                            item.setChildren(getChildren(categoryEntityList, item.getCatId()));
                            return item;
                        }
                        //按sort排序
                ).sorted((o1, o2) -> ( o1.getSort()==null?0:o1.getSort()) - (o2.getSort()==null?0: o2.getSort()) )

                .collect(Collectors.toList());

    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

}