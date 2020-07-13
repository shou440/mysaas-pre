package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyWMeterRecord {

    /**
     * 水表用水记录
     */
    private Integer  room_id;
    private Integer  meter_id;
    private float    water_cur;
    private float    water_last;
    private float    water_used;
    private Timestamp fresh_time;

}
