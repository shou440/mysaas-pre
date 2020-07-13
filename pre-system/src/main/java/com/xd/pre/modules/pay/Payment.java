package com.xd.pre.modules.pay;

import java.util.concurrent.LinkedBlockingQueue;

public class Payment implements IPayment {



    //支付单的信息
    protected PaymentInfo payment_info = null;

    //支付状态
    protected int pay_state = PAY_STATE_PREPARE;

    //错误描述
    protected String error_message = "";

    //支付成功通知
    protected  boolean success_notify = false;

    //支付单消息缓冲
    private LinkedBlockingQueue<IPayment> newpayment_queue = new LinkedBlockingQueue<IPayment>();

    @Override
    public PaymentInfo getPaymentInfo() {
        return payment_info;
    }

    @Override
    public void NotifyCmplt() {
        success_notify = true;
    }


    @Override
    public boolean Save(){


        return false;
    }


    @Override
    public String getLastError() {
        return error_message;
    }

    //支付回调函数
    @Override
    public void CallTick() {



    }
}
