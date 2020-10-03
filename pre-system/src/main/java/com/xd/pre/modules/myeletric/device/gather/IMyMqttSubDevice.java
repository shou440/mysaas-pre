package com.xd.pre.modules.myeletric.device.gather;

import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import org.aspectj.bridge.ICommand;

//MQtt设备接口,用于轮询
public interface IMyMqttSubDevice {

    //关联的设备
    IDevice getDevice();

    //获取数据物联网关
    IDeviceGather gather();

    //是否需要校时
    boolean IsNeedClock();

    //是否需要重新发送
    boolean IsNeedResend();

    //上一次更新数据的时间
    int LastFreshTick();

    //发送数据请求命令
    void SendCallQuestCmd();

    //发送命令请求数据
    void SendCommand(IMyCommand command);

    //清除剩余电度
    void ClearLeftDDCommand(IMyCommand command);

    //清除累计电度
    void ClearTotalDDCommand(IMyCommand command);

    //调整电度
    void AdjustDDCommand(IMyCommand command);

    //处理接收到的数据
    boolean ProcessData(byte[] data);

    //处理接收到的数据
    boolean ProcessCommand(byte[] data,IMyCommand command);

    //检查接收的数据是否正确
    boolean checkReceive(byte[] data);

}
