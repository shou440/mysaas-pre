package com.xd.pre.modules.myeletric.domain;

//电费计费单

import lombok.Data;
import java.sql.Timestamp;

@Data
public class MyMeterFee {

    private Integer    area_id;
    private Integer    room_id;
    private Integer    meter_id;
    private Integer    user_id;   //业主ID
    private String     fee_sn;    //流水号
    private String     tenant_id;
    private String    payment_id;
    private String     area_city;
    private String     area_name;
    private String     room_name;
    private String     tenant_name;
    private String     tenant_openid;
    private float      ep_start;
    private float      ep_end;
    private float      ep_used;
    private float      ep_price;
    private float      total_fee;
    private Integer    fee_status;
    private Timestamp  time_start;
    private Timestamp  time_end;
    private Timestamp  time_crt;
    private Timestamp  time_upt;
}
