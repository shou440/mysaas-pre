package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MyMeterBasePriceDto {

    private int      meter_id;
    private float    ep_base;
    private float    ep_price;
}
