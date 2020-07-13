package com.xd.pre.modules.pay;

//支付接口
public interface IPayment {

    /************************************************************************************************************
     *                      支付状态
     *************************************************************************************************************/
    public  static final int PAY_STATE_PREPARE          = 0x00;    //准备状态
    public  static final int PAY_STATE_PAY_QUERY        = 0x01;    //实时查询支付结果
    public  static final int PAY_STATE_CMPLT            = 0x02;    //支付完成
    public  static final int PAY_STATE_CANCEL           = 0x03;    //客户取消订单
    public  static final int PAY_STATE_ABORT            = 0x04;    //支付异常
    public  static final int PAY_STATE_DISPOSE          = 0x05;    //待释放

    /************************************************************************************************************
     *                      支付类型
     *************************************************************************************************************/
    public  static final int PAY_TYPE_WX         = 0x00;    //微信支付
    public  static final int PAY_TYPE_ALIPAY     = 0x01;    //支付宝
    public  static final int PAY_TYPE_INVALIDATE = 0xFF;    //微信支付


    //获取支付单信息
    PaymentInfo getPaymentInfo();

    //微信通知支付成功
    void NotifyCmplt();

    //创建支付订单
    boolean Save();

   //支付回调函数
    void CallTick();

    //获取错误信息
    String getLastError();



}
