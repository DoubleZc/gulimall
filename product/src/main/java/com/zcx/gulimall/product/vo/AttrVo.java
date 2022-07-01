package com.zcx.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.zcx.gulimall.product.entity.AttrEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class AttrVo extends AttrEntity {
    /***
     * 分组id
     *
     */
    @Getter
    @Setter
    private Long attrGroupId;

}
