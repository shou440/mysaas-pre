package com.xd.pre.modules.myeletric.device.channel;


//通道发送线程
public class ChannelSendThread extends Thread {

    private IMyChannel channel = null;

    public ChannelSendThread(){

    }

    public ChannelSendThread(IMyChannel channelTmp)
    {
        channel = channelTmp;
    }


    @Override
    public void run() {

        if (null != channel)
        {
            channel.SendThreadFun();
        }
    }
}
