package com.xd.pre.modules.myeletric.dto;


//查询园区或者房间电费的参数

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Data
public class MyMeterFeeQueryDto {

    private Integer    area_id;
    private Integer    room_id;
    private Integer    meter_id;
    private String     room_tenant_id;    //租赁合同流水号18位,6位room号+12位的年月日时分秒

    private Timestamp end_time;


    private  Timestamp start_time;
}
