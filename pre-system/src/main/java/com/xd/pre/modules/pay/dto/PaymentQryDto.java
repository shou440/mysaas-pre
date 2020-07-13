package com.xd.pre.modules.pay.dto;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
public class PaymentQryDto {

    private String payment_id;
    private Integer user_id;
    private Timestamp start_time;
    private Timestamp end_time;
    private String tenant_openid;

}
