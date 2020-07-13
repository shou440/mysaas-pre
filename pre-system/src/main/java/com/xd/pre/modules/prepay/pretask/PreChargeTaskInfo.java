package com.xd.pre.modules.prepay.pretask;


//电费充值任务描述
public class PreChargeTaskInfo {


    //任务状态:预支付，支付查询，充值中，完成，异常
    private int task_state;

    //充值表号
    private int meter_id;

    //微信用户openid
    private String open_id;

    //充值金额
    private int charge_fee;

    //预支付单号，20位号,2位充值类型+12位年月日时分秒+6位电表号=20位
    private String payment_id;

    //创建订单时间
    private int start_tick;

    //错误描述
    private String err_dec;
}
