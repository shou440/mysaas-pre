package com.xd.pre.modules.myeletric.device.command;

import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import org.aspectj.bridge.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

//用户命令容器单件对象
public class CommandContainer implements Runnable{

    //系统命令集合
    List<IMyCommand> lst_command = new ArrayList<IMyCommand>();

    //用户新的命令同步消息队列
    private LinkedBlockingQueue<IMyCommand> new_command_queue = new LinkedBlockingQueue<IMyCommand>();

    private boolean isWorking = false;

    private boolean has_started = false;


    //任务线程
    private Thread thread_task = null;


    //单件对象
    public static CommandContainer sinTon = null;

    //获取单件对象
    public static CommandContainer getInstance()
    {
        if (null == sinTon)
        {
            sinTon = new CommandContainer();
        }

        return sinTon;
    }


    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        if (has_started)
        {
            return false;
        }

        has_started  =true;


        //启动工作线程
        isWorking = true;
        thread_task = new Thread(this);
        thread_task.start();

        return  true;
    }

    //工作线程
    @Override
    public void run() {

        while (isWorking)
        {
            //去除要删除的命令
            List<IMyCommand> lstDispose = new ArrayList<IMyCommand>();


            for(int i = 0; i < lst_command.size(); i++)
            {
                IMyCommand command = lst_command.get(i);
                if (null != command)
                {
                    if (!command.IsNeedDispose())
                    {
                        command.CallTick();
                    }
                    else
                    {
                        lstDispose.add(command);
                    }
                }
            }

            //将已经完成或异常的命令从队列中删除
            for(int i = 0; i < lstDispose.size(); i++)
            {
                IMyCommand command = lstDispose.get(i);
                if(null != command)
                {
                    lst_command.remove(command);
                }
            }

            //添加用户新加入的命令
            while (new_command_queue.size() > 0)
            {
                IMyCommand command = new_command_queue.poll();
                if (null != command)
                {
                    lst_command.add(command);
                }
            }

            try {
                Thread.sleep(100);
            }
            catch (Exception ex)
            {

            }
        }

    }
}
