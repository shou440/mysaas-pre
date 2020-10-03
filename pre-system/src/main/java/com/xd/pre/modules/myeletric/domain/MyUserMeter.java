package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyUserMeter {
    /**
     * 用户购买电表映射表
     */

    private Integer  meter_id;
    private Integer  user_id;
    private String  pay_id;
    private Timestamp crt_time;
    private Timestamp upt_time;
}
