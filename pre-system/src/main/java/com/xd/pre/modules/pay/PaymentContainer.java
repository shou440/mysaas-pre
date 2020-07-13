package com.xd.pre.modules.pay;

import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.domain.MyWaterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.service.IMyMeterFeeService;
import com.xd.pre.modules.myeletric.service.IWaterFeeService;
import com.xd.pre.modules.pay.dto.PaymentUptParam;
import com.xd.pre.modules.pay.mapper.MyPaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.weixin4j.model.pay.WCPay;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Component
public class PaymentContainer implements Runnable {

    @Autowired
    private MyPaymentMapper ipaymentMapper ;

    @Autowired
    private IMyMeterFeeService myMeterFeeService;

    @Autowired
    private IWaterFeeService myWaterFeeService;



    //系统支付单队列缓冲
    private List<IPayment> pay_lst = new ArrayList<IPayment>();

    //单件对象
    private static PaymentContainer sinTon=null;

    //操作支付单集合同步锁
    private Object Payment_LOCK = new Object();

    //任务线程
    private Thread thread_task = null;

    private boolean isWorking = false;

    private boolean has_started=false;


    //新添加的支付单队列

    private LinkedBlockingQueue<IPayment> newpayment_queue = new LinkedBlockingQueue<IPayment>();


    //消息队列
    private SynchronousQueue msg_queue = new SynchronousQueue();

    public static PaymentContainer GetThePaymentContainer()
    {
        if(null == sinTon)
        {
            sinTon = new PaymentContainer();
        }

        return  sinTon;
    }

    //设置数据库存储对象
    public void setMapper(MyPaymentMapper payMapper,
                          IMyMeterFeeService meterFeeService,
                          IWaterFeeService waterFeeService
                          )
    {
        ipaymentMapper = payMapper;
        myMeterFeeService = meterFeeService;
        myWaterFeeService = waterFeeService;
    }

    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        if (has_started)
        {
            return false;
        }



        //从数据库中装载产品
        LoadLockedPayment();



        //启动工作线程
        isWorking = true;
        thread_task = new Thread(this);
        thread_task.start();

        return  true;
    }

    //装载锁定的支付单
    public  void LoadLockedPayment()
    {

    }

    //更新订单的支付状态
    @Transactional
    public boolean UptPayment(PaymentInfo paymentInfo)
    {
        if (null == paymentInfo)
        {
            return false;
        }

        try
        {
            //判断支付原因，如果是费用单，则更新费用单的状态
            if (paymentInfo.getPayment_reason().equals("payfee"))
            {
                MyMeterFeeUptParam uptParam = new MyMeterFeeUptParam();
                uptParam.setPayment_id(paymentInfo.getPayment_id());
                if (paymentInfo.getPayment_status() == IPayment.PAY_STATE_CMPLT)
                {
                    uptParam.setFee_status(5);

                }
                else if (paymentInfo.getPayment_status() == IPayment.PAY_STATE_CANCEL)
                {
                    uptParam.setFee_status(1);
                }

                PaymentUptParam payParam = new PaymentUptParam();
                payParam.setPayment_id(paymentInfo.getPayment_id());
                payParam.setPayment_status(paymentInfo.getPayment_status());

                myMeterFeeService.unLockByPaymentResult(uptParam);
                myWaterFeeService.unLockByPaymentResult(uptParam);
                ipaymentMapper.updatePaymentStatus(payParam);
            }

            //更新支付单的结果



            return true;

        }
        catch (Exception ex)
        {
            return false;
        }

        //如果是预付费则不用




    }

    //创建缴费单的支付订单
    @Transactional
    public WCPay CreateFeePayment(List<MyMeterFee> meterFeeList,
                                    List<MyWaterFee> waterFeeList,
                                    String openid,
                                    String sMessage,
                                    float fTotalFee,
                                    int userid)
    {
        //获取当前连接的用户ID
        try {

            //创建支付订单

            String paymentid = String.format("%06d%016d",userid, CommonFun.GetTick());
            WCPay ret = MyWeixinStub.getTheMyWeixinStub().getUnifiedOrder(openid,paymentid,sMessage,fTotalFee);

            if (ret == null)
            {

                return  null;
            }


            Timestamp tm = new Timestamp(System.currentTimeMillis());
            PaymentInfo payInfo = new PaymentInfo();
            payInfo.setPayment_id(paymentid);
            payInfo.setPayment_sn(paymentid);
            payInfo.setPayment_type("weixin");
            payInfo.setPayment_count(openid);
            payInfo.setReceive_count("userid_"+String.format("%06d",userid));
            payInfo.setUser_id(userid);
            payInfo.setPayment_fee((int)(fTotalFee*100));
            payInfo.setPayment_status(0);
            payInfo.setPayment_memo(sMessage);
            payInfo.setTime_crt(tm);
            payInfo.setPayment_reason("payfee");    //设置原因为支付费单


            ipaymentMapper.createNewPayment(payInfo);         //将收费单记录进数据库中


            //更新电费单的支付状态
            List<MyMeterFeeUptParam> lstParam = new ArrayList<MyMeterFeeUptParam>();
            for(int i = 0; i < meterFeeList.size();i++)
            {
                MyMeterFee fee = meterFeeList.get(i);
                if (null != fee)
                {
                    MyMeterFeeUptParam uptParam = new MyMeterFeeUptParam();
                    uptParam.setFee_sn(fee.getFee_sn());
                    uptParam.setPayment_id(paymentid);
                    uptParam.setFee_status(4);
                    uptParam.setQuest_upt_time(tm);
                    lstParam.add(uptParam);
                }
            }
            myMeterFeeService.batchupdatePayment(lstParam);


            //更新水费单的支付状态
            lstParam = new ArrayList<MyMeterFeeUptParam>();
            for(int i = 0; i < waterFeeList.size();i++)
            {
                MyWaterFee fee = waterFeeList.get(i);
                if (null != fee)
                {
                    MyMeterFeeUptParam uptParam = new MyMeterFeeUptParam();
                    uptParam.setFee_sn(fee.getFee_sn());
                    uptParam.setPayment_id(paymentid);
                    uptParam.setFee_status(4);
                    uptParam.setQuest_upt_time(tm);
                    lstParam.add(uptParam);
                }
            }
            myWaterFeeService.batchupdatePayment(lstParam);


            //预支付申请成功后，创建支付单的电子单据,并保存到数据库中，并添加支付单到系统缓冲
            IPayment pay = new WXPayment(payInfo);
            PaymentContainer.GetThePaymentContainer().addPayment(pay);

            WCPay retPay = new WCPay(ret,paymentid);

            return  retPay;

        } catch (Exception ex) {

            System.out.print("缴费测试异常:"+ex.getMessage()+ex.getStackTrace().toString());
            return null;
        }
    }

    //添加支付单
    public boolean addPayment(IPayment pay)
    {

        try {
            newpayment_queue.put(pay);
            return true;
        } catch (InterruptedException e) {
            return  false;
        }


    }

    //根据支付单号获取支付单
    public IPayment getPayment(String paymentid)
    {
        IPayment pay = null;
        int nLen = pay_lst.size();

        for(int i = 0; i < nLen; i++)
        {
            pay = pay_lst.get(i);
            if(null != pay
                    && pay.getPaymentInfo() != null
                    && pay.getPaymentInfo().getPayment_id().equals(paymentid))
            {
                return pay;
            }

        }
        return  pay;
    }

    //任务回调函数
    public  void CallTick() {


        //判断是否有需要新加入的支付单,有则放入系统缓冲
        if (newpayment_queue.size() > 0)
        {
            IPayment pay = (IPayment)newpayment_queue.poll();
            if (null != pay)
            {
                pay_lst.add(pay);
            }
        }

        //循环调用支付单的回调函数
        List<IPayment>  left_lst = new ArrayList<IPayment>();
        int nLen = pay_lst.size();
        List<IPayment> dispose_lst = new ArrayList<IPayment>();
        for(int i = 0; i < nLen; i++)
        {
            IPayment pay = pay_lst.get(i);
            if (null != pay)
            {
                pay.CallTick();
            }

            if (pay.getPaymentInfo() != null &&  pay.getPaymentInfo().getPayment_status() != IPayment.PAY_STATE_DISPOSE)
            {
                left_lst.add(pay);
            }

        }

        //更新队列
        pay_lst = left_lst;

    }


    @Override
    public void run() {

        isWorking =true;
        while (isWorking)
        {
            CallTick();

            try
            {
                Thread.sleep(100);
            }
            catch (Exception ex)
            {

            }
        }



    }

}
