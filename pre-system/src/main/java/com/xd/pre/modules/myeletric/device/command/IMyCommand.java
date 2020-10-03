package com.xd.pre.modules.myeletric.device.command;


import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductFunction;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;

//设备操作命令
public interface IMyCommand {


    //命令状态
    public static final int  STATE_INIT = 0x00;
    public static final int  STATE_PREPARE = 0x01;
    public static final int  STATE_COMMITING = 0x02;
    public static final int  STATE_CMPLT = 0x03;
    public static final int  STATE_CANCEL = 0x04;
    public static final int  STATE_ABORT = 0x05;
    public static final int  STATE_DISPOSE = 0x10;


    //命令类型
    public static final int  COMMAND_INVALID                 = 0x00;    //无效命令
    public static final int  COMMAND_CHARGE                  = 0x01;    //网络充值
    public static final int  COMMAND_MANNER_ADJUST           = 0x02;    //电度调整
    public static final int  COMMAND_MANNER_CLEARLEFT        = 0x03;    //清零剩余电度
    public static final int  COMMAND_MANNER_CLEAR_TOTALEP    = 0x04;    //清零累计电度
    public static final int  COMMAND_DO                      =  0x05;    //遥控操作


    Integer getCommandType();
    Integer getStartTick();

    //命令的SN
    Integer getCommandSN();

    //超时时间
    Integer ExipredTick();

    boolean isExpired();

    //获取命令的状态
    Integer getState();

    //错误描述
    String  getErrMsg();


    //函数的名称
    String getFunctionName();

    //获取执行命令的相关的设备
    IDevice getDevice();

    //执行命令的函数
    IProductFunction getFunction();

    //获取命令信息对象
    MyCommandInfo getCommandInfo();

    //回调函数
    void CallTick();

    //卸载操作命令标志
    boolean IsNeedDispose();

    //接收Gather的消息
    void onMessage(MyCommandMessage msg);

    //接收Gather的消息
    MyCommandMessage fetchMessage();

    //刷新Command的状态到Redis
    void freshRedis();
}
