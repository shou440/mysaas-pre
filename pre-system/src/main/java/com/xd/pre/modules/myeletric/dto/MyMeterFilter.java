package com.xd.pre.modules.myeletric.dto;


//电表过滤参数

import lombok.Data;

@Data
public class MyMeterFilter {

    private Integer area_id;
    private Integer room_id;
    private Integer meter_id;
    private String  tenant_openid;

}
