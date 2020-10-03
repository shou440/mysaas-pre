package com.xd.pre.modules.myeletric.device.channel;

import com.xd.pre.modules.myeletric.device.channel.filter.IChannelFilter;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;

public interface IMyChannel {

    int getChannelID();

    void  setName(String Name);
    String getName();

    //发送数据
    void sendData(Object data);

    //接收数据回调函数
    void OnReceive(Object data);

    //启动通道
    boolean startChannel();

    //停止通道
    void stopChannel();

    //发送线程
    void SendThreadFun();

    //接收线程
    void RecThreadFun();

    //添加设备规约解析器
    int addGather(IDeviceGather gather);

    //解除设备规约解析器
    int detachGather(IDeviceGather gather);

    //添加数据过滤器,主要用于做
    boolean addFilter(IChannelFilter filter);

    //解除过滤器
    void detachFilter(IChannelFilter filter);
}
