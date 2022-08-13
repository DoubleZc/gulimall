package com.zcx.gulimall.coupon.service.impl;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.zcx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.zcx.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String promotionSessionId = params.get("promotionSessionId").toString();
        QueryWrapper<SeckillSkuRelationEntity> wrapper = new QueryWrapper<>();
        if (Strings.isNotEmpty(promotionSessionId))
        {
            wrapper.eq("promotion_session_id",Long.valueOf(promotionSessionId));
        }
    
    
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}