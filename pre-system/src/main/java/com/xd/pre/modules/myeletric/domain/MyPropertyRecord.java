package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyPropertyRecord {

    /**
     * 设备属性历史记录
     */
    private String  product_name;           //产品名称
    private String  device_name;            //设备名称
    private String  property_name;          //属性名称
    private float   property_value;         //属性值类型
    private int     is_valid ;              //是否有效记录
    private int     save_tick;              //保存时间
}
