package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyWaterFee {

    private Integer    area_id;
    private Integer    room_id;
    private Integer    meter_id;
    private Integer    user_id;
    private String     tenant_id;
    private String     fee_sn;//流水号
    private String    payment_id;
    private String     area_city;
    private String     area_name;
    private String     room_name;
    private String     tenant_name;
    private String     tenant_openid;
    private float      water_start;
    private float      water_end;
    private float      water_used;
    private float      water_price;
    private float      total_fee;
    private Integer    fee_status;
    private Timestamp time_start;
    private Timestamp  time_end;
    private Timestamp  time_crt;
    private Timestamp  time_upt;
}
