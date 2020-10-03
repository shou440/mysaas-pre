package com.xd.pre.modules.pay;

import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.pay.dto.PaymentUptParam;

import java.util.concurrent.LinkedBlockingQueue;

public class Payment implements IPayment {



    //支付单的信息
    protected PaymentInfo payment_info = null;

    //支付状态
    protected int pay_state = PAY_STATE_PREPARE;

    //错误描述
    protected String error_message = "";

    //支付取消通知
    protected  boolean success_notify = false;

    //支付成功通知
    protected  boolean cancel_notify = false;

    //支付关联的命令，如电表充值
    protected IMyCommand device_command = null;

    //支付单消息缓冲
    private LinkedBlockingQueue<IPayment> newpayment_queue = new LinkedBlockingQueue<IPayment>();

    protected void SwitchState(int nNextState)
    {
        pay_state = nNextState;

        if (null != payment_info && nNextState != IPayment.PAY_STATE_DISPOSE)
        {
            //刷新实时数据库
            freshRedis();

            //更新支付单状态
            PaymentUptParam uptParam = new PaymentUptParam();
            payment_info.setPayment_status(pay_state);
            PaymentContainer.GetThePaymentContainer().UptPayment(payment_info);
        }

    }

    @Override
    public PaymentInfo getPaymentInfo() {
        return payment_info;
    }

    @Override
    public int getState() {
        return pay_state;
    }

    @Override
    public void NotifyCmplt() {
        success_notify = true;
    }

    @Override
    public void NotifyCancel() {
        cancel_notify = true;
    }


    @Override
    public boolean Save(){


        return false;
    }


    @Override
    public String getLastError() {
        return error_message;
    }


    //刷新支付单在Redis的实时状态
    @Override
    public void freshRedis() {

        if (null == payment_info || getState() == IPayment.PAY_STATE_DISPOSE)
        {
            return;
        }


        String key = "paymentstate_"+payment_info.getPayment_id();
        Integer nState = getState();
        String sState = String.format("%d",nState);
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(key,sState);

    }

    @Override
    public void setCommand(IMyCommand command) {
        device_command = command;
    }

    @Override
    public IMyCommand getCommand() {
        return device_command;
    }

    //支付回调函数
    @Override
    public void CallTick() {



    }
}
