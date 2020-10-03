package com.xd.pre.modules.pay;

import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.command.CommandContainer;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import com.xd.pre.modules.pay.promotion.MyPromotion;
import com.xd.pre.modules.sys.domain.SysUser;
import org.weixin4j.model.pay.OrderQueryResult;

public class WXPayment extends Payment {

    private int  expire_tick = 0;
    private long last_query_tick = 0;
    private long  query_gap = 500;
    private int  query_times  = 0;

    //默认构造函数
    public WXPayment(PaymentInfo pay)
    {
        payment_info = pay;
        expire_tick = CommonFun.GetTick()+60;
    }

    //判断是否超期
    protected boolean IsExpire()
    {
        if (expire_tick < CommonFun.GetTick())
        {
            return  true;
        }

        return false;
    }


    //检查支付单是否支付成功,支付的超期时间为1分钟，超过1分钟则认为用户订单支付失败
    protected void State_Query()
    {
        //如果通知成功，则直接进入成功状态
        if (success_notify)
        {
            SwitchState(PAY_STATE_CMPLT);
            return;
        }

        //如果取消则
        if(cancel_notify)
        {
            SwitchState(PAY_STATE_CANCEL);
            return;
        }

        //判断是否超期
        if (IsExpire())
        {
            System.out.print("支付异常:超期\n");
            SwitchState(PAY_STATE_ABORT);
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
            error_message = "支付单信息没设置";
            System.out.print("支付异常:支付单信息没设置");
            SwitchState(PAY_STATE_ABORT);

            return;
        }

        //查询支付结果
        OrderQueryResult ret = MyWeixinStub.getTheMyWeixinStub().QueryPay(payInfo.getPayment_id());


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
                    SwitchState(PAY_STATE_CMPLT);
                }
                else if ( ret.getTrade_state().equals("CLOSED"))        //交易超时关闭，解锁费单
                {

                    error_message = "支付超时关闭";
                    System.out.print("支付异常：支付超时关闭");
                    SwitchState(PAY_STATE_ABORT);
                }
                else if ( ret.getTrade_state().equals("REVOKED"))        //交易撤销
                {

                    error_message = "支付撤销";
                    SwitchState(PAY_STATE_CANCEL);


                }
                else if (ret.getTrade_state().equals("PAYERROR"))       //交易错误
                {

                    error_message = "支付错误";
                    System.out.print("支付异常：支付错误");
                    SwitchState(PAY_STATE_ABORT);


                }
            }


        }
    }

    //执行成功后，写入Redis查询结果,并查看是否有关联的设备命令，如果有则执行设备命令，如充值
    protected void state_cmplt()
    {

        SwitchState(IPayment.PAY_STATE_PROMOTION_PREPARE);

    }
    //提成准备
    protected void state_promotion_prepare() {

        String  trade_no = getPaymentInfo().getPayment_id()+"99";
        String  openid = getPaymentInfo().getUser_count();
        int  countType = getPaymentInfo().getCount_type();
        float fFee = getPaymentInfo().getPayment_fee()/1000.0f;
        MyWeixinStub.getTheMyWeixinStub().userPromotion(openid,trade_no,"结算水电费",fFee);

        SwitchState(IPayment.PAY_STATE_DISPOSE);
    }

    //查询提成是否到账
    protected void state_promotion() {



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
                    error_message = "支付单信息没设置";
                    SwitchState(PAY_STATE_ABORT);
                }
                else
                {
                    SwitchState(PAY_STATE_PAY_QUERY);
                }

                break;
            }
            case PAY_STATE_PAY_QUERY:                   //实时查询
            {
                State_Query();
                break;
            }
            case PAY_STATE_CMPLT:
            {
                state_cmplt();
                break;
            }
            case PAY_STATE_PROMOTION_PREPARE:     //结算准备
            {
                state_promotion_prepare();
                break;
            }
            case PAY_STATE_PROMOTION:            //结算查询中
            {
                state_promotion();
                break;
            }
            case PAY_STATE_CANCEL:      //取消
            {
                break;
            }
            case PAY_STATE_ABORT:      //支付异常
            {
                break;
            }

        }

    }

}
