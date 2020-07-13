package com.xd.pre.modules.prepay.pretask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

//预付费充值容器
public class PreChargeTaskContainer {

    //单件对象
    private static PreChargeTaskContainer sinTon=null;

    //电表的充值任务队列缓冲
    private List<PreChargeTask> lst = new ArrayList<>();

    //消息队列
    private SynchronousQueue msg_queue = new SynchronousQueue();

    public static PreChargeTaskContainer GetTheTaskContainer()
    {
        if(null == sinTon)
        {
            sinTon = new PreChargeTaskContainer();
        }

        return  sinTon;
    }


    //从Redis中装载充值任务，用于系统重启用
    public  void LoadFromRedis()
    {

    }


    //创建新的充值任务
    public boolean CreateNewTask(String openid,int nMeterID,int nFee)
    {
        return  false;
    }


    //任务回调函数,系统采用消息模式加入新的任务和销毁任务，保证了任务队列的同步性
    public  void CallTick()
    {
        for(int i = 0; i<lst.size(); i++)
        {

        }

        //检查消息队列中是否有新的任务，如果有则加入队列中
        PreChargeTask task =  (PreChargeTask)msg_queue.poll();
        if (null != task)
        {
            task.CallTick();
        }

        //检查是否有需要销毁的任务





    }

}
