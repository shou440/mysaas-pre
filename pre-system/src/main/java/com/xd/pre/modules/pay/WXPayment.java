package com.xd.pre.modules.pay;

import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.pay.dto.PaymentUptParam;
import org.weixin4j.model.pay.OrderQueryResult;

public class WXPayment extends Payment {

    private long last_query_tick = 0;
    private long  query_gap = 500;
    private int  query_times  = 0;

    //默认构造函数
    public WXPayment(PaymentInfo pay)
    {
        payment_info = pay;
    }

    private void SwitchState(int nNextState)
    {
        pay_state = nNextState;
    }

    //支付成功，更新支付状态
    private  boolean uptStatus()
    {
        return false;
    }

    //支付失败或撤销，更新支付状态


    //检查支付单是否支付成功
    private void State_Query()
    {
        //如果通知成功，则直接进入成功状态
        if (success_notify)
        {
            SwitchState(PAY_STATE_CMPLT);
            return;
        }

        long gap = CommonFun.GetMsTick() - last_query_tick;
        if (gap < query_gap)
        {
            return;
        }
        else
        {
            last_query_tick = CommonFun.GetMsTick();
        }

        //查询10次以后每5秒间隔才查询
        if (++query_times > 10)
        {
            query_gap = 3000;
        }

        PaymentInfo payInfo = getPaymentInfo();
        if (payInfo == null)
        {
            SwitchState(PAY_STATE_ABORT);
            error_message = "支付单信息没设置";
            return;
        }

        //查询支付结果
        OrderQueryResult ret = MyWeixinStub.getTheMyWeixinStub().QueryPay(payInfo.getPayment_id());
        boolean isNeedUpt = false;

        if (null != ret)
        {

            if (ret.getReturn_code().equals("SUCCESS")                  //只有这4个都有结果才有下面的参数内容
                    && ret.getReturn_msg().equals("OK")
                    && ret.getResult_code().equals("SUCCESS")
                   )
            {
                if (ret.getTrade_state().equals("SUCCESS"))             //支付成功，修改水费单和电费单
                {
                    //判断支付单的原因是支付费单，则修改费单的状态
                    System.out.print("查询支付成功");
                    SwitchState(PAY_STATE_CMPLT);

                    isNeedUpt = true;

                    //
                }
                else if ( ret.getTrade_state().equals("CLOSED"))        //交易超时关闭，解锁费单
                {
                    System.out.print("查询支付中止CLOSED");
                    SwitchState(PAY_STATE_ABORT);
                    error_message = "支付超时关闭";
                    isNeedUpt = true;
                }
                else if ( ret.getTrade_state().equals("REVOKED"))        //交易撤销
                {
                    System.out.print("查询支付中止REVOKED");
                    SwitchState(PAY_STATE_CANCEL);
                    error_message = "支付撤销";
                    isNeedUpt = true;
                }
                else if (ret.getTrade_state().equals("PAYERROR"))       //交易错误
                {
                    System.out.print("查询支付中止PAYERROR");
                    SwitchState(PAY_STATE_ABORT);
                    error_message = "支付错误";
                    isNeedUpt = true;
                }
            }

            if(isNeedUpt)
            {
                System.out.print("更新支付单状态和费单状态");
                PaymentUptParam uptParam = new PaymentUptParam();
                payInfo.setPayment_status(pay_state);
                PaymentContainer.GetThePaymentContainer().UptPayment(payInfo);
            }
        }
    }

    //支付回调函数
    @Override
    public void CallTick() {

        switch (pay_state)
        {
            case PAY_STATE_PREPARE:             //就绪状态
            {
                if (payment_info == null)
                {
                    pay_state = PAY_STATE_ABORT;
                    error_message = "支付单信息没设置";
                }
                else
                {
                    pay_state = PAY_STATE_PAY_QUERY;
                }

                break;
            }
            case PAY_STATE_PAY_QUERY:                   //实时查询
            {
                State_Query();
                break;
            }

        }

    }

}
