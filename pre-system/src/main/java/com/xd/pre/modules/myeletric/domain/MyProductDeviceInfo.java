package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyProductDeviceInfo {

    /**
     * 设备信息
     */

    private String  product_name;
    private String  device_name;               //设备名称
    private String  device_group;              //设备分组
    private int     device_type;              //设备类型:0,子设备,1:网关设备
    private String  device_key ;              //设备访问Key
    private  int    device_no;                //设备号
    private String  gateway_name ;            //网关名称
    private String        gateway_channel ;         //网关通道
    private String     device_memo;           //备注,可以记录网关的卡号
    private Timestamp     crt_time;               //创建时间
    private Timestamp     upt_time;           //更新时间


}
