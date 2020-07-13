package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

@Data
public class MyRoomTenantVo
{
    private Integer   room_id;           //房间编号
    private String    room_tenant_id;    //租赁合同流水号18位,6位room号+12位的年月日时分秒

}
