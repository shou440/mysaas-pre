package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

//产品信号
@Data
public class MyProductSignalInfo {

    /**
     * 产品信号
     */

    private String  product_name;
    private String  signal_name;              //信号名称
    private String  value0_dec ;             //0值描述
    private String  value1_dec ;             //1值描述
    private String  signal_dec;              //信号描述
    private int     upt_tick;                //更新时间
}
