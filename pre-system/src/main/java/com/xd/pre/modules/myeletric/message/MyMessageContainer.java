package com.xd.pre.modules.myeletric.message;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

//整个系统的消息容器，包括费单通知、充值通知、设备异常等
public class MyMessageContainer implements Runnable{

    //新的数据采集器请求注册队列，需要多线程同步
    private LinkedBlockingQueue<IMyMessage> new_message_queue = new LinkedBlockingQueue<IMyMessage>();

    //数据采集器集合
    List<IMyMessage> lst_message = new ArrayList<IMyMessage>();

    //单件对象
    public static MyMessageContainer sinTon = null;

    //任务线程
    private Thread thread_task = null;

    //获取单件对象
    public static MyMessageContainer getSinTon()
    {
        if (null == sinTon)
        {
            sinTon = new  MyMessageContainer();
        }

        return  sinTon;
    }

    //添加消息
    public  void AddMessage(IMyMessage msg)
    {
        if(null != msg)
        {
            try
            {
                new_message_queue.put(msg);
            }
            catch (Exception ex)
            {

            }

        }
    }

    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        thread_task = new Thread(this);
        thread_task.start();

        return  true;
    }

    @Override
    public void run() {

        while(true)
        {

            try
            {
                while (new_message_queue.size() != 0)
                {
                    IMyMessage msg = new_message_queue.poll();
                    if (null != msg)
                    {
                        lst_message.add(msg);
                    }
                }

                //提出已经处理完毕的消息
                List<IMyMessage> lstCmplt = new ArrayList<IMyMessage>();
                for(int i = 0; i < lst_message.size(); i++)
                {
                    IMyMessage msg = lst_message.get(i);
                    if (null != msg)
                    {
                        msg.ProcessMessage();

                        if (msg.hasProcessedCmplt())
                        {
                            lstCmplt.add(msg);
                        }
                    }
                }

                //删除已经处理的消息
                for(int i = 0; i < lstCmplt.size(); i++)
                {
                    IMyMessage msg = lstCmplt.get(i);
                    if (null != msg)
                    {
                        lst_message.remove(msg);
                    }
                }

                Thread.sleep(1000);
            }
            catch (Exception ex)
            {

            }

        }


    }
}
