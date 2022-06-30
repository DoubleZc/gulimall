package com.zcx.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.zcx.common.valid.AddGroup;
import com.zcx.common.valid.ListValues;
import com.zcx.common.valid.UpdateGroup;
import com.zcx.common.valid.UpdateStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:35
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "修改必须携带id", groups = {UpdateGroup.class,UpdateGroup.class})
    @Null(message = "新增必须无id", groups = {AddGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @Null(groups = {UpdateStatus.class})
    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class,UpdateGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @Null(groups = {UpdateStatus.class})
    @NotNull(groups = {AddGroup.class})
    @URL(message = "logo地址不合法",groups = {AddGroup.class,UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ListValues(values = {0, 1},groups = {AddGroup.class,UpdateGroup.class, UpdateStatus.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @Null(groups = {UpdateStatus.class})
    @NotNull(groups = {AddGroup.class})
    @Pattern(regexp = "[a-zA-Z]", message = "一个字母",groups = {AddGroup.class,UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @Null(groups = {UpdateStatus.class})
    @NotNull(groups = {AddGroup.class})
    @Min(value = 0, message = "排序必须大于0",groups = {AddGroup.class,UpdateGroup.class})
    private Integer sort;

}
