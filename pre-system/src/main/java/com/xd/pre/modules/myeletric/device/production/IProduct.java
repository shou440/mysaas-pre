package com.xd.pre.modules.myeletric.device.production;

//产品接口

import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;

import java.util.List;

public interface IProduct {

    //产品类型
    int PRODUCT_TYPE_P2P     = 0x00;         //0:直连
    int PRODUCT_TYPE_GATEWAY = 0x01;         //1:网关设备
    int PRODUCT_TYPE_SUBDEVICE = 0x02;         //1:网关设备

    //产品名称
    String getProduct_name();
    void setProduct_name(String name);

    //产品种类
    String getProduct_class();
    void setProduct_class(String sClass);

    //产品类型
    int getProduct_type();
    void setProduct_type(int nType);

    //产品描述
    String getProduct_dec();
    void setProduct_dec(String dec);

    //产品密匙
    String getProductKey();
    void setProduct_key(String sKey);

    //产品规约
    String getProductProtocal();
    void setProductProtocal(String sProtocal);

    //获取设备的所有属性
    List<IProductProperty> getPropertys();
    IProductProperty getProperty(String sPropertyName);
    void AddProperty(IProductProperty productProperty);

    //获取设备的功能
    List<IProductFunction> getFunctions();
    IProductFunction getFunction(String sFunctionName);
    void AddFunction(IProductFunction fun);

    //获取设备的信号
    List<IProductSignal> getSignals();
    IProductSignal getSignal(String sSignalName);
    void AddSignal(IProductSignal signal);

    //设备配置的事件列表
    List<MyProductEvent> getEvents();
    MyProductEvent getEvent(String sEventName);
    void AddEvent(MyProductEvent event);

    //创建设备
    IDevice CreateDevice(MyProductDeviceInfo info);
    boolean AddDevice(MyProductDeviceInfo info);
    List<IDevice> getAllDevice();
    IDevice getDevice(String deviceName);

    //1创建P2P数据采集器、2创建数据网关采集器、3创建网关子设备数据采集器(由产品容器调用，由于子设备需要先创建
    //   网关数据采集器然后再挂接上去，所以有先后次序
    boolean CreateP2PGather();
    boolean CreateGatewayGather();

}

