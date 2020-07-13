package com.xd.pre.modules.myeletric.domain;

import lombok.Data;
import java.sql.Timestamp;

//电表和租赁
@Data
public class MyMeterTenantTb {

    /**
     * 电表数据
     */
    private Integer  room_id;
    private Integer  meter_id;
    private float    ep_last;
    private String   room_tenant_id;    //租赁合同流水号18位,6位room号+12位的年月日时分秒
    private Timestamp meter_upt_date;
}
