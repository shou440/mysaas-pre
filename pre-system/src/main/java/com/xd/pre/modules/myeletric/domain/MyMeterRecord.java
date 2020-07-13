package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

//电表用电记录
@Data
public class MyMeterRecord {

    /**
     * 电表用电记录
     */
    private Integer  room_id;
    private Integer  meter_id;
    private float    ep_cur;

    private Timestamp fresh_time;

}
