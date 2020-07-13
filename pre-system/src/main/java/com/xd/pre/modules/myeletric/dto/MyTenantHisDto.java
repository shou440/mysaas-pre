package com.xd.pre.modules.myeletric.dto;

import lombok.Data;
import org.joda.time.DateTime;

import java.sql.Timestamp;

@Data
public class MyTenantHisDto {

    private Integer   room_id;                  //房间编号
    private String    room_tenant_id;           //租赁合同编号
    private String    room_name;                //房间名称
    private String    tenant_name;              //租户名称
    private String    tenant_tel;               //租户手机号
    private String    tenant_contactor;         //联系人
    private String    tenant_openid;            //租户微信号
    private Timestamp tenant_time;              //租赁起始时间
    private Timestamp  crt_time;                 //创建合同时间
    private Timestamp  end_time;                 //租赁结束
}
