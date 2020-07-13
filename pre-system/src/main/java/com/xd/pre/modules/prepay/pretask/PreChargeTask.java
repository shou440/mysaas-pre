package com.xd.pre.modules.prepay.pretask;

import com.xd.pre.modules.pay.IPayment;

//预付费充值任务
public class PreChargeTask {

    //支付任务状态
    public  static final int  TASK_STATE_PRE                 = 0x00;              //预支付状态
    public  static final int  TASK_STATE_PAY                 = 0x01;              //支付状态
    public  static final int  TASK_STATE_COMMIT              = 0x02;              //执行状态
    public  static final int  TASK_STATE_PAYTO_OWNER         = 0x03;              //转发给业主
    public  static final int  TASK_STATE_ABORT               = 0x04;              //异常

    //支付单
    private IPayment charge_payment =  null;
    
    //任务信息
    private PreChargeTaskInfo task;

    //创建
    private int task_state = TASK_STATE_PRE;

    //回调函数
    public void CallTick()
    {
        switch (task_state)
        {
            case TASK_STATE_PRE:              //预支付状态
            {
                break;
            }
            case TASK_STATE_PAY:              //支付状态
            {
                break;
            }
            case TASK_STATE_COMMIT:           //执行状态
            {
                break;
            }
            case TASK_STATE_PAYTO_OWNER:     //转账给业主
            {
                break;
            }
            case TASK_STATE_ABORT:           //执行异常
            {
                break;
            }
        }
    }


}
