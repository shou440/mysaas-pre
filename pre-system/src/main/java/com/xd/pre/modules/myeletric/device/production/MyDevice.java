package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.gather.*;
import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class MyDevice implements IDevice {

    /*******************************************************************************************************************
                        基本属性
     *******************************************************************************************************************/
    protected String            product_name="";
    protected String             dev_group="";                    //设备分组
    protected int                dev_type = IDevice.DEVICE_TYPE_SUBDEVICE;
    protected String             device_name="";
    protected String             device_key="";
    protected int                device_no=0;
    protected String             gateway_channel = "";
    protected String             device_gateway = "";
    protected String             device_version="Ver_0.0.0";
    protected String             device_dec = "";
    protected IDeviceGather      gather = null;
    protected IProduct           device_product = null;
    protected int                last_registe_tick = 0;      //最近一次网络注册的时间
    protected boolean            is_online=false;
    protected int                online_expire=600;          //默认10分钟掉线


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
    public boolean IsBusy() {

        IDeviceGather gather = getGather();
        if (null == gather)
        {
            return false;
        }
        return gather.isBusy();
    }

    //在线标志
    @Override
    public boolean IsOnline() {
        return is_online;
    }

    //最近一次注册上线的时间
    @Override
    public int LastRegistTick() {
        return last_registe_tick;
    }

    //注册网络信号
    @Override
    public void OnLineRegiste() {

        last_registe_tick = CommonFun.GetTick();
        is_online = true;

    }

    //设备网络信号检测回调函数
    @Override
    public void OnLineCheck() {

        int nGap = CommonFun.GetTick() - last_registe_tick;
        if (nGap > online_expire)
        {
            is_online = false;
        }

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


        int nLen = lst_signal.size();
        for(int i = 0; i < nLen; i++)
        {
            IProductSignal signal = lst_signal.get(i);
            if (null != signal && signal.getSignalName().equals(sSignalName))
            {
                return  signal;
            }
        }

        return  null;
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
    public void setEvents(List<MyProductEvent> events) {
        lst_event = events;
    }

    //创建设备，根据产品种类不通构建过程不同(包含三类P2P直连设备、网关设备、网关子设备)
    @Override
    public boolean CreateDevice() {
        return false;
    }

    @Override
    public IDeviceGather getGather() {
        return gather;
    }


    //创建设备数据采集器
    @Override
    public IDeviceGather CreateGather() {

        //判断设备是否为网关设备，网关设备则创建网关数据采集器
        if (getDeviceType() == IDevice.DEVICE_TYPE_GATEWAY)
        {
            if (getProductName().equals("MYGW-100") )
            {
                gather = new MyMqttMeterGather(this);

                return gather;
            }
        }

        return null;
    }

    //创建网关子设备
    @Override
    public IMyMqttSubDevice CreateSubDevice() {

        if (null == device_product || device_product.getProduct_type() != MyProduct.PRODUCT_TYPE_SUBDEVICE)
        {
            return null;
        }


        if (device_product.getProduct_name().equals("MY610-ENB"))
        {
            IDeviceGather gather = ProductionContainer.getTheMeterDeviceContainer().getGather(getGatewayName());
            if (null == gather)
            {
                return null;
            }
            IMyMqttSubDevice subDevice = new My610ESubDevice(this,gather);
            return subDevice;
        }
        else if (device_product.getProduct_name().equals("MY630-ENB"))
        {
            IDeviceGather gather = ProductionContainer.getTheMeterDeviceContainer().getGather(getGatewayName());
            if (null == gather)
            {
                return null;
            }
            IMyMqttSubDevice subDevice = new My630ESubDevice(this,gather);
            return subDevice;
        }
        return null;
    }





    //设备插入命令
    @Override
    public void onCommand(IMyCommand command) {


        //提取网关设备，将命令缓存到网关设备中
        IDeviceGather gather = ProductionContainer.getTheMeterDeviceContainer().getGather(getGatewayName());
        if (null != gather)
        {
            gather.onCommand(command);

        }
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

        device_dec = info.getDevice_memo();
        device_key = info.getDevice_key();
        device_no = info.getDevice_no();
        device_gateway = info.getGateway_name();
        gateway_channel = info.getGateway_channel();

        device_product = product;
    }

}
