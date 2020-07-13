package com.xd.pre.modules.myeletric.domain;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class MyWMeter
{

    /**
     * 水表数据
     */

    private Integer  meter_id;
    private Integer  room_id;
    private String   meter_sn;
    private String   meter_type;
    private float    meter_scale;
    private Integer  meter_status;
    private String   meter_dec;
    private float    water_base;
    private float    water_last;
    private float    water_price;
    private DateTime meter_crt_date;
    private DateTime meter_upt_date;
}
