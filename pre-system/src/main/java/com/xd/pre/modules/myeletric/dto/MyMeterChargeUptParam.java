package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

import java.sql.Timestamp;

//电表充值单更新参数

@Data
public class MyMeterChargeUptParam {

    /**
     * 更新电表充值单:有几种可能需要更新收费单的状态
     *                   1、创新新的充值单
     *                   2、支付成功后的标记
     *                   3、充值单失败标记
     *                   4、支付单失败标记
     */

    private Integer   meter_id;
    private String    payment_id;
    private Integer   charge_status;
    private Integer    charge_sn;
    private Timestamp upt_time;
}
