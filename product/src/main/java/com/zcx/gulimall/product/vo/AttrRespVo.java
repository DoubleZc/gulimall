package com.zcx.gulimall.product.vo;

import lombok.Getter;
import lombok.Setter;

public class AttrRespVo extends AttrVo{


    @Getter
    @Setter
    private  String catelogName;

    @Getter
    @Setter
    private String groupName;



    @Getter
    @Setter
    private Long[] catelogPath;

    @Getter
    @Setter
    private Long attrGroupId;


}
