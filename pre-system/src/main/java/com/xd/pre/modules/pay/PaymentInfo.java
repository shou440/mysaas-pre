package com.xd.pre.modules.pay;

import lombok.Data;

import java.sql.Timestamp;

//支付单信息
@Data
public class PaymentInfo {

    //支付单号
    private String  payment_id;
    private String  payment_sn;
    private String  payment_type;
    private String  payment_reason;
    private String  payment_count;
    private String  receive_count;
    private Integer user_id;
    private Integer payment_fee;
    private Integer payment_status;
    private String  payment_memo;

    private Timestamp  time_crt;
}
