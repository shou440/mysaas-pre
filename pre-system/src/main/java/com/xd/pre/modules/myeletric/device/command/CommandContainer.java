package com.xd.pre.modules.myeletric.device.command;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.dto.MeterCommandDto;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

//用户命令容器单件对象
public class CommandContainer implements Runnable{



    @Autowired
    private IMyMeterService myMeterService;

    //判断命令缓冲是否失败
    private boolean has_setup = false;

    //当前最大的命令sn
    private Integer max_sn  = 0;

    //系统命令集合
    List<IMyCommand> lst_command = new ArrayList<IMyCommand>();

    //用户新的命令同步消息队列
    private LinkedBlockingQueue<IMyCommand> new_command_queue = new LinkedBlockingQueue<IMyCommand>();

    //命令消息队列
    protected LinkedBlockingQueue<MyCommandMessage> queue_new_msg = new LinkedBlockingQueue<MyCommandMessage>();


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

    //设置数据库存储对象
    public  void SetMapper(IMyMeterService meterService)
    {
        myMeterService = meterService;
    }

    //创建新的命令
    public void AddNewCommand(IMyCommand command)
    {
        try {
            new_command_queue.put(command);
        }
        catch (Exception ex)
        {

        }

    }


    //获取新的命令序号
    public Integer getNewSN()
    {
        int nMaxSN = max_sn+1;
        String key = "max_sn";
        String sState = String.format("%d",nMaxSN);
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(key,sState,true);
        max_sn = nMaxSN;
        return max_sn;

    }

    //从Redis中读取最大的命令编号
    public boolean LoadMaxSN()
    {
        max_sn = MyDbStub.getInstance().getMaxCommandSN();
        if (0 != max_sn)
        {
            return true;
        }

        return  false;
    }

    //创建命令工厂方法,根据命令参数创建电表操作命令
    public IMyCommand CreateCommand(MeterCommandDto meterCommandDto)
    {


        String sDeviceName = String.format("Meter%06d",meterCommandDto.getMeter_id());
        IDevice meterDevice = ProductionContainer.getTheMeterDeviceContainer().getDevice(sDeviceName);
        String str = String.format("查找电表设备失败10"+sDeviceName+"\n");
        System.out.print(str);
        if (null == meterDevice)
        {
            str = String.format("查找电表设备失败11:"+sDeviceName+"\n");
            System.out.print(str);
            return null;
        }


        IMyCommand command = null;
        MyCommandInfo info = new MyCommandInfo();
        info.setCommand_sn(meterCommandDto.getCommand_sn());
        info.setProduct_name(meterDevice.getProductName());
        info.setDevice_name(meterDevice.getDeviceName());
        info.setStart_tick(CommonFun.GetTick());
        info.setUser_id(meterCommandDto.getUser_id());
        info.setErr_msg("");
        info.setExpire_tick(CommonFun.GetTick()+30);

        str = String.format("查找电表设备失败12:%s\n",sDeviceName);
        System.out.print(str);

        switch (meterCommandDto.getCommand_id())
        {
            case IMyCommand.COMMAND_CHARGE:                     //网络充值命令
            {
                command = new MyChargeCommand(info);
                break;
            }
            case IMyCommand.COMMAND_MANNER_CLEAR_TOTALEP:      //清除累计电度
            {
                command = new MyClearTotalEpCommand(info);
                break;
            }
            case IMyCommand.COMMAND_MANNER_ADJUST:      //清除累计电度
            {
                command = new MyMeterDrawbackCommand(info);
                break;
            }
            case IMyCommand.COMMAND_MANNER_CLEARLEFT:      //清除累计电度
            {
                command = new MyClearLeftEPCommand(info);
                break;
            }
        }

        return command;
    }

    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        if (has_started)
        {
            return false;
        }

        //装载最大的命令序列号
        if (!LoadMaxSN())
        {
            System.out.print("启动命令失败\n");
           // return  false;
        }

        has_started  =true;
        has_setup = true;

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

            //调用命令回调函数
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
                    System.out.print("添加新命令\n");
                    lst_command.add(command);
                }
            }

            try {
                Thread.sleep(20);
            }
            catch (Exception ex)
            {

            }
        }

    }
}
