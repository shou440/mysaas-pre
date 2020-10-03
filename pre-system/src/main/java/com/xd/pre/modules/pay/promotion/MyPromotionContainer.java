package com.xd.pre.modules.pay.promotion;

import com.xd.pre.modules.pay.IPayment;
import com.xd.pre.modules.pay.mapper.MyPromotionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class MyPromotionContainer implements Runnable {

    @Autowired
    private MyPromotionMapper myPromotionMapper ;

    //结算队列缓冲
    private List<MyPromotion> promotions = new ArrayList<MyPromotion>();

    //单件对象
    private static MyPromotionContainer sinTon=null;


    //任务线程
    private Thread thread_task = null;

    private boolean isWorking = false;

    private boolean has_started=false;


    //新添加的支付单队列

    private LinkedBlockingQueue<MyPromotion> newpromotion_queue = new LinkedBlockingQueue<MyPromotion>();


    //消息队列
    private SynchronousQueue msg_queue = new SynchronousQueue();

    public static MyPromotionContainer getSinTon()
    {
        if(null == sinTon)
        {
            sinTon = new MyPromotionContainer();
        }

        return  sinTon;
    }

    //设置数据库存储对象
    public void setMapper(MyPromotionMapper myPromotionMapper1)
    {
        myPromotionMapper = myPromotionMapper1;

    }

    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        if (has_started)
        {
            return false;
        }

        //启动工作线程
        isWorking = true;
        thread_task = new Thread(this);
        thread_task.start();

        return  true;
    }


    //任务回调函数
    public  void CallTick() {

        //判断是否有新的
        if (newpromotion_queue.size() > 0)
        {
            MyPromotion promotion = (MyPromotion)newpromotion_queue.poll();
            if (null != promotion)
            {
                promotions.add(promotion);
            }
        }

        //循环调用结算单的回调函数
        List<MyPromotion>  left_lst = new ArrayList<MyPromotion>();
        int nLen = promotions.size();
        List<MyPromotion> dispose_lst = new ArrayList<MyPromotion>();
        for(int i = 0; i < nLen; i++)
        {
            MyPromotion promotion = promotions.get(i);
            if (null != promotion)
            {
                promotion.CallTcik();
            }

            if (promotion.getPromotionInfo() != null &&  promotion.getState() != MyPromotion.STATE_DISPOSE)
            {
                left_lst.add(promotion);
            }

        }

        //更新队列
        promotions = left_lst;

    }

    //运行函数
    @Override
    public void run() {

        isWorking =true;
        while (isWorking)
        {
            CallTick();

            try
            {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {

            }
        }

    }
}
