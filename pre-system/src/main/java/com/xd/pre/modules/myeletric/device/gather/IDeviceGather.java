package com.xd.pre.modules.myeletric.device.gather;

//网关设备数据采集器
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;

public interface IDeviceGather {

    String getName();

    //获取采集器的通道
    IMyChannel getChannel();

    //绑定采集器的通讯通道
    boolean bindChannel();

    //采集器回调函数
    void callTick();

    //获取数据网关的设备数据
    IDevice getGateWayDevice();

    //数据采集器是否忙
    boolean isBusy();

    //挂接新的设备
    void AddNewSubDevice(IDevice device);

    //获取子设备
    IDevice GetSubDevice(String productName,String sDeviceName);

    //数据接收函数
    void onReceive(Object data);

    //发送数据
    void SendData(Object data);


    //接收用户命令
    void onCommand(IMyCommand command);
}
