package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MeterChargeDto {

    private  Integer meter_id;
    private  float   charge_fee;
    private  String  tenant_openid;
    private Integer  command_sn;
}
