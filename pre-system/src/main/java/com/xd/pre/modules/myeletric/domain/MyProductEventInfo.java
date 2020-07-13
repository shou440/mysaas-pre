package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyProductEventInfo {

    /**
     * 产品事件
     */

    private String  product_name;
    private String  event_name;              //事件名称
    private String  event_dec ;              //事件描述
    private String  event_type ;             //事件类型
    private int     upt_tick;                //更新时间
}
