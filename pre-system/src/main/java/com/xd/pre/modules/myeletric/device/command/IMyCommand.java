package com.xd.pre.modules.myeletric.device.command;


import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductFunction;

//设备操作命令
public interface IMyCommand {

    //获取执行命令的相关的设备
    IDevice getDevice();

    //执行命令的函数
    IProductFunction getFunction();

    //回调函数
    void CallTick();

    //卸载操作命令标志
    boolean IsNeedDispose();


}
