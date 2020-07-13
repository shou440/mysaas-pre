package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class MyMeterRdQryDto {

    private Integer   meter_id;
    private Timestamp start_time;
    private Timestamp end_time;

}
