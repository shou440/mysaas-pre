package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.gather.MyMqttMeterGather;
import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class MyDevice implements IDevice {

    /*******************************************************************************************************************
                        基本属性
     *******************************************************************************************************************/
    String     product_name="";
    String     dev_group="";                    //设备分组
    int        dev_type = IDevice.DEVICE_TYPE_SUBDEVICE;
    String     device_name="";
    String     device_key="";
    int        sub_index=0;
    int        device_no=0;
    String     gateway_channel = "";
    String     device_gateway = "";
    String     device_version="Ver_0.0.0";
    boolean    is_online=false;
    int       online_tick =0;
    String    device_dec = "";
    IDeviceGather gather = null;
    IProduct  device_product = null;


    //属性列表
    List<IProductProperty>  lst_property = new ArrayList<IProductProperty>();

    //功能列表
    List<IProductFunction> lst_function = new ArrayList<IProductFunction>();

    //信号列表
    List<IProductSignal>  lst_signal = new ArrayList<IProductSignal>();

    //事件列表
    List<MyProductEvent> lst_event = new ArrayList<MyProductEvent>();




    @Override
    public String getProductName() {
        return product_name;
    }

    @Override
    public void setProductName(String name) {
        product_name = name;

    }

    @Override
    public String getDeviceName() {
        return device_name;
    }

    @Override
    public void setDeviceName(String name) {
        device_name = name;
    }

    @Override
    public String getDeviceGroup() {
        return dev_group;
    }

    @Override
    public void setDeviceGroup(String group) {
        dev_group = group;
    }

    @Override
    public int getDeviceType() {
        return dev_type;
    }

    @Override
    public void setDeviceType(int nType) {
        dev_type = nType;
    }

    @Override
    public String getDeviceKey() {
        return device_key;
    }

    @Override
    public void setDeviceKey(String key) {
        device_key = key;
    }

    @Override
    public int getSubIndex() {
        return sub_index;
    }

    @Override
    public void setSubIndex(int index) {
        sub_index = index;
    }

    @Override
    public int getDevNO() {
        return device_no;
    }

    @Override
    public void setDevNO(int nNO) {
        device_no = nNO;
    }

    @Override
    public String getDeviceVersion() {
        return device_version;
    }

    @Override
    public void setDeviceVersion(String version) {
        device_version = version;
    }

    @Override
    public void setOnline(boolean online) {
        is_online = online;
    }

    @Override
    public boolean isOnline() {
        return is_online;
    }

    @Override
    public int getOnlineTick() {
        return online_tick;
    }

    @Override
    public void setOnlineTick(int nTick) {
        online_tick = nTick;
    }

    @Override
    public String getDeviceDec() {
        return device_dec;
    }

    @Override
    public void setDeviceDec(String dec) {
        device_dec = dec;
    }

    @Override
    public String getChannelName() {
        return gateway_channel;
    }

    @Override
    public String getGatewayName() {
        return device_gateway;
    }

    @Override
    public IProduct getProduct() {
        return device_product;
    }
    @Override
    public List<IProductProperty> getPropertys() {
        return  lst_property;

    }
    @Override
    public IProductProperty getProperty(String sPropertyName) {
        List<IProductProperty> proLst = new ArrayList<IProductProperty>();

        lst_property.forEach(e-> {

            if (null != e && e.getPropertyName().equals(sPropertyName))
            {
                proLst.add(e);
                return;
            }
        });

        if (proLst.size() >= 1)
        {
            return proLst.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setPropertys(List<IProductProperty> lst) {
        lst_property = lst;
    }

    @Override
    public List<IProductFunction> getFunctions() {
        return lst_function;
    }

    @Override
    public IProductFunction getFunction(String sFunctionName)
    {
        List<IProductFunction> funLst = new ArrayList<IProductFunction>();

        lst_function.forEach(e-> {

            if (null != e && e.getFunctionName() == sFunctionName)
            {
                funLst.add(e);
                return;
            }
        });

        if (funLst.size() >= 1)
        {
            return funLst.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void addFunction(IProductFunction function) {

    }

    @Override
    public List<IProductSignal> getSignals() {
        return lst_signal;
    }

    @Override
    public IProductSignal getSignal(String sSignalName) {

        List<IProductSignal> signalLst = new ArrayList<IProductSignal>();

        lst_signal.forEach(e-> {

            if (null != e && e.getSignalName() == sSignalName)
            {
                signalLst.add(e);
                return;
            }
        });

        if (signalLst.size() >= 1)
        {
            return signalLst.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setSignals(List<IProductSignal> lst) {
        lst_signal = lst;
    }

    @Override
    public List<MyProductEvent> getEvents() {
        return lst_event;
    }

    @Override
    public MyProductEvent getEvent(String sEventName) {

        List<MyProductEvent> eventLst = new ArrayList<MyProductEvent>();

        lst_event.forEach(e-> {

            if (null != e && e.event_name == sEventName)
            {
                eventLst.add(e);
                return;
            }
        });

        if (eventLst.size() >= 1)
        {
            return eventLst.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void addEvent(MyProductEvent event) {

    }

    @Override
    public IDeviceGather getGather() {
        return gather;
    }


    //创建设备数据采集器
    @Override
    public boolean CreateGather() {

        //判断设备是否为网关设备，网关设备则创建网关数据采集器
        if (getDeviceType() == IDevice.DEVICE_TYPE_GATEWAY)
        {
            if (getProductName().equals("MYGW-100") )
            {
                gather = new MyMqttMeterGather(this);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean IsOnline() {
        return is_online;
    }

    @Override
    public int onlineCheckTick() {
        return online_tick;
    }


    @Override
    public void CallTick() {

    }

    public MyDevice(){

    }

    public MyDevice(MyProductDeviceInfo info,IProduct product)
    {
        product_name = info.getProduct_name();
        device_name = info.getDevice_name();
        dev_group = info.getDevice_group();
        dev_type = info.getDevice_type();

        device_dec = info.getDevice_dec();
        device_key = info.getDevice_key();
        sub_index = info.getSub_index();
        device_no = info.getDevice_no();
        device_gateway = info.getGateway_name();
        gateway_channel = info.getGateway_channel();

        device_product = product;
    }

}
