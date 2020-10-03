package com.xd.pre.modules.pay.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyPromotionInfo {
    private String     promotion_id;        //提成单ID
    private String     promotion_type;     //提成类型
    private float      total_fee;          //结算金额
    private float      sx_fee;             //手续费
    private float      promotion_fee;      //提成金额
    private int        user_id;            //用户ID
    private String     user_name;
    private String     user_count;         //结算账户
    private String     promotion_memo;
    private Timestamp  crt_time;
}
