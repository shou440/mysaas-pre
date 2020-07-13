package com.xd.pre.modules.prepay.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreMeterDto {
    private Integer  meter_id;
    private String   meter_type;
    private String   meter_sn;
    private String   meter_addr;
    private String   room_name;
    private String   owner_openid;
    private String   owner_tel;
    private String   manange_openid;
    private String   manage_tel;
    private String   tenant_openid;
    private String   tenant_tel;
    private String   tenant1_openid;
    private String   tenant1_tel;
    private float    ep_price;
    private float    ep_scale;
    private Timestamp meter_upt_date;
    private String   memo;
}
