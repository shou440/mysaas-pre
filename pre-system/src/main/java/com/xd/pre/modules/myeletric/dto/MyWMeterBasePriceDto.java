package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MyWMeterBasePriceDto {

    private int      meter_id;
    private float    water_base;
    private float    water_price;
}
