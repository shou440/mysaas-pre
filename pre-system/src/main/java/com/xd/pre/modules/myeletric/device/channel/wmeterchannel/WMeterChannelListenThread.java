package com.xd.pre.modules.myeletric.device.channel.wmeterchannel;


import com.xd.pre.modules.myeletric.device.channel.IMyChannel;

//水表数据采集通道的侦听线程
public class WMeterChannelListenThread extends Thread{

    private WMeterUDPChannel channel = null;

    public WMeterChannelListenThread(WMeterUDPChannel channelTmp)
    {
        channel = channelTmp;
    }

    @Override
    public void run() {

        if (null != channel)
        {
            channel.RecThreadFun();
        }
    }

}
