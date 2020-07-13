package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyProductPropertyInfo {

    /**
     * 产品属性
     */

    private String  product_name;
    private String  property_name;           //属性名称
    private String  property_dec;           //属性描述
    private int     value_type ;             //属性值类型
    private int     float_bits;              //小数点位数
    private String  value_weight;            //单位
    private int     upt_tick;                //更新时间

}
