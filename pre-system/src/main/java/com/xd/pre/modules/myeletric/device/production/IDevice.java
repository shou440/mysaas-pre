package com.xd.pre.modules.myeletric.device.production;


import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.gather.IMyMqttSubDevice;

import java.util.HashMap;
import java.util.List;

//设备接口
public interface IDevice {

    public static final int  DEVICE_TYPE_SUBDEVICE = 0;        //设备类型:子设备
    public static final int  DEVICE_TYPE_GATEWAY = 1;          //设备类型:网关设备

    //属性
    String getProductName();
    void setProductName(String name);

    String getDeviceName();
    void setDeviceName(String name);

    String getDeviceGroup( );
    void setDeviceGroup(String group);

    int getDeviceType();
    void    setDeviceType(int nType);

    String getDeviceKey();
    void setDeviceKey(String key);


    int getDevNO();
    void setDevNO(int nNO);

    String getDeviceVersion();
    void setDeviceVersion(String version);




    String getDeviceDec();
    void setDeviceDec(String dec);

    String getGatewayName();
    String getChannelName();

    //获取设备的产品
    IProduct getProduct();

    //获取设备的所有属性
    List<IProductProperty> getPropertys();
    IProductProperty getProperty(String sPropertyName);
    void setPropertys(List<IProductProperty> lst);

    //获取设备的功能
    List<IProductFunction> getFunctions();
    IProductFunction getFunction(String sFunctionName);
    void addFunction(IProductFunction function);

    //获取设备的信号
    List<IProductSignal> getSignals();
    IProductSignal getSignal(String sSignalName);
    void setSignals(List<IProductSignal> lst);



    //获取设备的事件
    List<MyProductEvent> getEvents();
    MyProductEvent getEvent(String sEventName);
    void addEvent(MyProductEvent event);
    void setEvents(List<MyProductEvent> events);

    //创建设备，根据产品种类不通构建过程不同(包含三类P2P直连设备、网关设备、网关子设备)
    boolean CreateDevice();

    //获取设备的数据采集器
    IDeviceGather getGather();
    IDeviceGather CreateGather();      //创建设备的数据采集器
    IMyMqttSubDevice CreateSubDevice();



    //设备是否忙
    boolean IsBusy();

    //上线更新时间
    int  LastRegistTick();            //最近注册的时间
    void OnLineRegiste();             //注册上线
    void OnLineCheck();           //检测是否在线的回调函数
    boolean IsOnline();               //返回设备是否在线

    void onCommand(IMyCommand command);
    void CallTick();                //回调函数

}
