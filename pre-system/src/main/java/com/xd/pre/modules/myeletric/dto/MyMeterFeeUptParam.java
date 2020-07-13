package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MyMeterFeeUptParam
{
    /**
     * 更新电表费单的参数:有几种可能需要更新收费单的状态
     *                   1、预支付单的申请
     *                   2、支付成功后的标记
     *                   3、业主的费单取消
     */

    private Integer   meter_id;
    private String    payment_id;
    private Integer   fee_status;
    private String    fee_sn;
    private Timestamp quest_upt_time;
}
