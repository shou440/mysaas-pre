package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyRoomTenantDto
{
    private Integer   room_id;           //房间编号
    private String    room_tenant_id;    //租赁合同流水号18位,6位room号+12位的年月日时分秒
    private Timestamp period_start_time;       //出租起始时间

}
