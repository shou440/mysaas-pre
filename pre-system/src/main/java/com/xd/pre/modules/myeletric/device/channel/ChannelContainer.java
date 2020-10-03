package com.xd.pre.modules.myeletric.device.channel;


import com.xd.pre.modules.myeletric.device.channel.wmeterchannel.WMeterUDPChannel;
import com.xd.pre.modules.myeletric.domain.MyChannelInfo;
import com.xd.pre.modules.myeletric.domain.MyChannelMqttInfo;
import com.xd.pre.modules.myeletric.mapper.MyChannelInfoMapper;
import com.xd.pre.modules.myeletric.mapper.MyMqttChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************************************
                系统通道容器
   主要用于通道的管理和维护
 ****************************************************************************************************/
public class ChannelContainer {

    public static final int MAX_CHANNEL = 50000;

    private boolean isWorking = false;

    private boolean has_started = false;

    //任务线程
    private Thread thread_task = null;

    //通道队列
    private List<IMyChannel>  channels = new ArrayList<IMyChannel>();

    //单件对象
    public static ChannelContainer sinTon = null;

    @Autowired
    private MyMqttChannelMapper myMqttChannelMapper ;

    @Autowired
    private MyChannelInfoMapper myChannelInfoMapper ;

    //获取单件对象
    public static  ChannelContainer getChannelContainer()
    {
        if (null == sinTon)
        {
            sinTon = new  ChannelContainer();
        }

        return  sinTon;
    }

    //设置数据库存储的对象Mapper
    public void setMapper(MyChannelInfoMapper channelMapper, MyMqttChannelMapper channelMqttMapper)
    {
        myChannelInfoMapper = channelMapper;
        myMqttChannelMapper = channelMqttMapper;
    }

    //通过通道号获取通道对象
    public  IMyChannel getChannel(int channel_id)
    {
        IMyChannel channel = null;
        for(int i = 0; i < channels.size(); i++)
        {
            channel = channels.get(i);
            if (channel.getChannelID() == channel_id)
            {
                return channel;
            }

        }

        return null;
    }

    //通过通道名称获取通道对象
    public  IMyChannel getChannel(String channel_name)
    {
        IMyChannel channel = null;
        for(int i = 0; i < channels.size(); i++)
        {
            channel = channels.get(i);
            if (channel.getName().equals(channel_name))
            {
                return channel;
            }

        }

        return null;
    }

    //从数据库中装载通道的配置信息
    public boolean loadChannel()
    {
        try
        {
            List<MyChannelInfo> lstChannel = myChannelInfoMapper.getAllChannels();
            for(int i = 0; i < lstChannel.size(); i++)
            {
                MyChannelInfo channelInfo = lstChannel.get(i);
                if (null != channelInfo)
                {
                    IMyChannel channel = createChannel(channelInfo);
                    if (null != channel)
                    {
                        if (getChannel(channel.getChannelID()) == null && getChannel(channel.getName()) == null)
                        {
                            channels.add(channel);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {

            String sErr = "";
            sErr = ex.getMessage();
        }

        return true;
    }

    //创建通道
    public IMyChannel createChannel(MyChannelInfo channelInfo)
    {
        List<MyChannelMqttInfo> mqttChannelInfoLst = myMqttChannelMapper.getChannel(channelInfo.getChannel_id());
        if (mqttChannelInfoLst.size() == 0)
        {
            return  null;
        }

        MyChannelMqttInfo mqttChannelInfo = mqttChannelInfoLst.get(0);
        if (null == mqttChannelInfo)
        {
            return null;
        }


        IMyChannel channel = null;
        if (channelInfo.getChannel_type().equals("mqtt"))
        {
            channel = new MyMqttChannel(mqttChannelInfo);
        }
        else if (channelInfo.getChannel_type().equals("bjhy"))   //北京慧怡
        {
            channel = new WMeterUDPChannel(channelInfo);
        }

        if (null != channel)
        {
            channel.setName(channelInfo.getChannel_name());
        }



        return channel;
    }

    //启动每个通道的服务
    public void startService()
    {
       for(int i = 0; i < channels.size(); i++)
       {
           IMyChannel channel = channels.get(i);
           if (null != channel)
           {
               channel.startChannel();
           }
       }

    }

}
