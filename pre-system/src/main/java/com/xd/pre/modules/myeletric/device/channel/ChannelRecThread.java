package com.xd.pre.modules.myeletric.device.channel;

import org.omg.CORBA.BAD_QOS;

public class ChannelRecThread extends Thread  {

    private IMyChannel channel = null;

    public ChannelRecThread(IMyChannel channelTmp)
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
