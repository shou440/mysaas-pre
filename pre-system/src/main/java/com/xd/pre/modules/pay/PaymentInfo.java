package com.xd.pre.modules.pay;

import lombok.Data;

import java.sql.Timestamp;

//支付单信息
@Data
public class PaymentInfo {

    //支付单号
    private String  payment_id;
    private String  promotion_id;
    private String  payment_type;
    private String  payment_room;

    private String  payment_reason;
    private String  payment_count;
    private String  receive_count;
    private String  room_name;
    private String  tenant_name;
    private Integer user_id;
    private Integer payment_fee;
    private Integer payment_status;
    private String  payment_memo;

    private String  promotion_sn;       //结算单号
    private String  user_count;         //结算账号
    private Integer count_type;         //账号类型,openid,mchid
    private String  promotion_time;     //结算日期

    private Timestamp  time_crt;
}
