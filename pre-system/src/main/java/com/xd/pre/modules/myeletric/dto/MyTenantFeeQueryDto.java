package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

import java.sql.Timestamp;


//租户查询电费，水费
@Data
public class MyTenantFeeQueryDto {

    private String open_id;
    private Timestamp end_time;
    private  Timestamp start_time;
}
