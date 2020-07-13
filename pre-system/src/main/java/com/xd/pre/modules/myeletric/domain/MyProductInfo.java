package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyProductInfo {

    /**
     * 产品数据
     */
    private String  product_name;
    private String  product_class;           //设备种类,0:电表,1:水表
    private String  product_key ;            //产品密匙
    private String  product_dec ;            //产品描述
    private int     product_type;            //产品类型,0:直连，1：网关
    private String  product_protocal;        //产品规约
    private int     upt_tick;                //更新时间
}
