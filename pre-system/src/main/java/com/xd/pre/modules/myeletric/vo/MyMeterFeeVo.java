package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

import java.sql.Timestamp;

//电表一段时间的累计电度统计
@Data
public class MyMeterFeeVo {

    private int        meter_id;
    private Timestamp start_time;
    private Timestamp  end_time;
    private  float     ep_start;
    private  float     ep_end;
    private  float     ep_used;

}
