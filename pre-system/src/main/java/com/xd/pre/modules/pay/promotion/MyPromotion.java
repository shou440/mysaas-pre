package com.xd.pre.modules.pay.promotion;

import com.xd.pre.modules.pay.vo.MyPromotionInfo;

import java.util.ArrayList;
import java.util.List;

public class MyPromotion {

    public static final int STATE_INIT        = 0x00;
    public static final int STATE_PROMOTION   = 0x01;
    public static final int STATE_CMPLT       = 0x02;
    public static final int STATE_ABORT       = 0xff;
    public static final int STATE_DISPOSE     = 0x03;

    //结算单信息
    private MyPromotionInfo promotionInfo;

    //结算的支付单
    private List<String> lst_paymentid = new ArrayList<String>();

    //结算单状态
    private int promotion_state = STATE_INIT;


    //上一次Tick时间
    private long last_tick = 0;

    //切换状态机状态
    private void SwitchState(int nNextState)
    {
        last_tick = System.currentTimeMillis();
        promotion_state = nNextState;
    }

    public MyPromotionInfo getPromotionInfo()
    {
        return promotionInfo;
    }

    public int getState()
    {
        return promotion_state;
    }

    //状态机:初始化
    public void State_Init()
    {

    }

    //状态机:支付结算中
    public void State_Promotion()
    {

    }

    //状态机:完成
    public void State_Cmplt()
    {

    }

    //状态机:异常
    public void State_Abort()
    {

    }

    //状态机:释放
    public void State_Dispose()
    {

    }

    //定时回调函数
    public void CallTcik()
    {
        switch (promotion_state)
        {
            case STATE_INIT:
            {
                State_Init();
                break;
            }
            case STATE_PROMOTION:
            {
                State_Promotion();
                break;
            }
            case STATE_CMPLT:
            {
                State_Cmplt();
                break;
            }
            case STATE_ABORT:
            {
                State_Abort();
                break;
            }
            case STATE_DISPOSE:
            {
                State_Dispose();
                break;
            }
        }
    }

}
