package com.xd.pre.modules.pay;

import com.xd.pre.modules.myeletric.device.command.IMyCommand;

//支付接口
public interface IPayment {

    /************************************************************************************************************
     *                      支付状态
     *************************************************************************************************************/
    public  static final int PAY_STATE_PREPARE             = 0x00;    //准备状态
    public  static final int PAY_STATE_PAY_QUERY           = 0x01;    //实时查询支付结果
    public  static final int PAY_STATE_CMPLT               = 0x02;    //支付完成
    public  static final int PAY_STATE_CANCEL              = 0x03;    //客户取消订单
    public  static final int PAY_STATE_ABORT               = 0x04;    //支付异常
    public  static final int PAY_STATE_DISPOSE             = 0x05;    //待释放

    public  static final int PAY_STATE_COMMIT_ABORT        = 0x10;    //支付成功，执行充值失败

    public  static final int PAY_STATE_PROMOTION_PREPARE   = 0x06;    //结算准备
    public  static final int PAY_STATE_PROMOTION           = 0x07;    //结算中
    public  static final int PAY_STATE_PROMOTION_CMPLT     = 0x08;    //结算完成
    public  static final int PAY_STATE_PROMOTION_ABORT     = 0xFE;    //结算异常
    public  static final int PAY_STATE_INVALID             = 0xFF;    //无效状态

    /************************************************************************************************************
     *                      支付类型
     *************************************************************************************************************/
    public  static final int PAY_TYPE_WX_PAY           = 0x00;    //微信在线支付
    public  static final int PAY_TYPE_WX_CACH          = 0x01;    //微信现金转账
    public  static final int PAY_TYPE_ALIPAY           = 0x02;    //支付宝在线支付
    public  static final int PAY_TYPE_ALIPAY_CACH      = 0x03;    //支付宝现金转账
    public  static final int PAY_TYPE_CACH             = 0x04;    //现金支付
    public  static final int PAY_TYPE_PERSONAL_COUNT   = 0x05;    //银行转私人账户
    public  static final int PAY_TYPE_COMPANY_COUNT    = 0x06;    //银行对公转账
    public  static final int PAY_TYPE_INVALIDATE       = 0x0F;    //无效支付


    //获取支付单信息
    PaymentInfo getPaymentInfo();

    //获取当前的支付状态
    int getState();

    //微信通知支付成功
    void NotifyCmplt();

    //支付取消通知
    void NotifyCancel();

    //创建支付订单
    boolean Save();

   //支付回调函数
    void CallTick();

    //获取错误信息
    String getLastError();

    //刷新支付单的状态
    void freshRedis();

    //设置命令
    void setCommand(IMyCommand command);

    //获取关联的设备命令
    IMyCommand getCommand();

}
