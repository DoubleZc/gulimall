package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.service.AttrService;
import com.zcx.gulimall.product.vo.BaseAttrs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.ProductAttrValueDao;
import com.zcx.gulimall.product.entity.ProductAttrValueEntity;
import com.zcx.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

	@Autowired
	private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveRelation(Long id, List<BaseAttrs> baseAttrs)
	{
		List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
			ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
			productAttrValueEntity.setAttrValue(item.getAttrValues());
			productAttrValueEntity.setQuickShow(item.getShowDesc());
			productAttrValueEntity.setAttrId(item.getAttrId());
			productAttrValueEntity.setSpuId(id);
			AttrEntity entity = attrService.getById(item.getAttrId());
			productAttrValueEntity.setAttrName(entity.getAttrName());
			return productAttrValueEntity;
		}).collect(Collectors.toList());
		saveBatch(collect);
	}

	@Override
	public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId)
	{

		List<ProductAttrValueEntity> list = list(new LambdaQueryWrapper<ProductAttrValueEntity>().eq(ProductAttrValueEntity::getSpuId, spuId));
		return list;


	}

	@Transactional
	@Override
	public void updateSpu(Long spuId,List<ProductAttrValueEntity> productAttrValueEntity)
	{
		LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ProductAttrValueEntity::getSpuId,spuId);
		remove(wrapper);

		productAttrValueEntity.forEach(i->{
			i.setSpuId(spuId);
		});

		saveBatch(productAttrValueEntity);

	}


}