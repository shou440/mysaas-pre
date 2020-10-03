package com.xd.pre.modules.myeletric.device.gather;

import com.xd.pre.modules.myeletric.device.channel.ChannelContainer;
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import org.joda.time.DateTime;

import java.util.concurrent.LinkedBlockingQueue;

//物联网关数据采集器
public class GatewayGather implements IDeviceGather {

    /*******************************************************************************************************************************************************
     *                 数据定义
     *
     *  1 meter_device,网关关联的设备
     *  2 last_callall_tick,上一次发送数据的时刻
     *  3 m_nRetryTimes,重试次数
     *  4 last_send_tick,最近一次发送数据的时间
     *  5 gather_name,数据采集器的名称
     *  6 time_last_callall,上一次轮询数据的时间
     *  7 cur_command,当前正在执行的命令
     *  8 queue_command,采集命令缓冲
     *  9 queue_new_subdevice,新挂接的网关子设备
     ***************************************************************************************************************************************/
    protected  IDevice    gateway_device = null;
    protected  int        last_callall_tick = 0;
    protected  int        m_nRetryTimes = 0;
    protected  int        last_send_tick = 0;
    protected  String     gather_name = "";
    protected  DateTime   time_last_callall = DateTime.now();

    protected IMyCommand cur_command = null;
    protected LinkedBlockingQueue<IMyCommand> queue_command = new LinkedBlockingQueue<IMyCommand>();
    protected LinkedBlockingQueue<IDevice> queue_new_subdevice = new LinkedBlockingQueue<IDevice>();

    @Override
    public String getName() {
        return gather_name;
    }

    //获取数据采集器对应的通讯通道
    @Override
    public IMyChannel getChannel() {

        if (null == gateway_device)
        {
            return null;
        }

        String sChannel = gateway_device.getChannelName();
        IMyChannel channel = ChannelContainer.getChannelContainer().getChannel(sChannel);
        return channel;
    }

    //绑定通讯通道
    @Override
    public boolean bindChannel() {

        //将自己添加到通道中
        IMyChannel channel = getChannel();
        if (null != channel)
        {
            channel.addGather(this);
        }

        return false;
    }

    @Override
    public void callTick() {

    }

    @Override
    public IDevice getGateWayDevice() {
        return gateway_device;
    }

    @Override
    public boolean isBusy() {

        if (cur_command == null)
        {
            return false;
        }
        return true;
    }

    //添加子设备
    @Override
    public void AddNewSubDevice(IDevice device) {

        if (null == device)
        {
            return;
        }
        try
        {
            queue_new_subdevice.put(device);
        }
        catch (Exception ex)
        {

        }

    }

    @Override
    public IDevice GetSubDevice(String productName, String sDeviceName) {
        return null;
    }

    @Override
    public void onReceive(Object data) {

    }

    @Override
    public void SendData(Object data) {

    }

    @Override
    public void onCommand(IMyCommand command) {

    }
}
