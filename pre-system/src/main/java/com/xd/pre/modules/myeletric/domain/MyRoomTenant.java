package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

//房间租赁表，每个房间一个记录
@Data
public class MyRoomTenant {

    private Integer   room_id;              //房间编号
    private String    room_tenant_id;       //租赁合同流水号18位,6位room号+12位的年月日时分秒
    private Timestamp period_start_time;    //计费周期起始时间
}
