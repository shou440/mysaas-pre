package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;
import com.xd.pre.modules.myeletric.domain.MyProductInfo;

import java.util.ArrayList;
import java.util.List;

public class MyProduct implements IProduct {

    //产品名称
    String     product_name = "";

    //产品种类
    String     product_class="";     //设备种类,0:电表,1:水表

    //产品描述
    String     product_dec="";     //设备种类,0:电表,1:水表

    //密匙
    String product_key="";

    //产品类型,0:直连，1：网关
    int  product_type=0;

    //规约类型
    String product_protocal = "";

    //设备列表
    List<IDevice> lst_device = new ArrayList<IDevice>();

    //属性列表
    List<IProductProperty>  lst_property = new ArrayList<IProductProperty>();

    //功能列表
    List<IProductFunction> lst_function = new ArrayList<IProductFunction>();

    //信号列表
    List<IProductSignal>  lst_signal = new ArrayList<IProductSignal>();

    //事件列表
    List<MyProductEvent>  lst_event= new ArrayList<MyProductEvent>();


    @Override
    public String getProduct_name() {
        return product_name;
    }

    @Override
    public void setProduct_name(String name) {
        product_name = name;
    }

    @Override
    public String getProduct_class() {
        return product_class;
    }

    @Override
    public void setProduct_class(String sClass) {
        product_class = sClass;
    }

    @Override
    public String getProduct_dec() {
        return product_dec;
    }

    @Override
    public void setProduct_dec(String dec) {
        product_dec = dec;
    }

    @Override
    public String getProductKey() {
        return product_key;
    }

    @Override
    public void setProduct_key(String sKey) {
        product_key = sKey;
    }

    @Override
    public String getProductProtocal() {
        return product_protocal;
    }

    @Override
    public void setProductProtocal(String sProtocal) {
        product_protocal= sProtocal;
    }



    //获取设备的所有属性
    @Override
    public List<IProductProperty> getPropertys() {
        return lst_property;
    }

    //获取设备属性
    @Override
    public IProductProperty getProperty(String sPropertyName) {

        List<IProductProperty> proLst = new ArrayList<IProductProperty>();

        lst_property.forEach(e-> {

            if (null != e && e.getPropertyName() == sPropertyName)
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
    public void AddProperty(IProductProperty productProperty) {
        if (null == productProperty)
        {
            return;
        }

        lst_property.add(productProperty);
    }

    @Override
    public List<IProductFunction> getFunctions() {
        return lst_function;
    }

    //获取功能列表
    @Override
    public IProductFunction getFunction(String sFunctionName) {
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
    public void AddFunction(IProductFunction fun) {
        lst_function.add(fun);
    }

    @Override
    public List<IProductSignal> getSignals() {
        return lst_signal;
    }

    @Override
    public IProductSignal getSignal(String sSignalName) {
        return null;
    }

    @Override
    public void AddSignal(IProductSignal signal) {
        lst_signal.add(signal);
    }

    @Override
    public List<MyProductEvent> getEvents() {
        return lst_event;
    }

    @Override
    public MyProductEvent getEvent(String sEventName) {
        return null;
    }

    @Override
    public void AddEvent(MyProductEvent event) {
        lst_event.add(event);
    }

    //创建设备对象
    @Override
    public IDevice CreateDevice(MyProductDeviceInfo info) {


        if (null == info)
        {
            return null;
        }

        MyDevice device = new MyDevice(info,this);

        //创建设备的属性
        List<IProductProperty> lstproperty = new ArrayList<IProductProperty>();
        for(int i = 0; i < lst_property.size(); i++)
        {
            IProductProperty property = lst_property.get(i);
            if (null != property)
            {
                IProductProperty propertyCopy = property.copy(info.getDevice_name());

                //获取属性在Redis中的名称，设备名称+"-"+属性名称
                String sProName = info.getDevice_name()+"-"+propertyCopy.getPropertyName();
                String sValue = MySystemRedisBuffer.getTheSinTon().getReisItemString(sProName);
                if (sValue != null && !sValue.equals(""))
                {
                    propertyCopy.setStringValue(sValue);
                }


                lstproperty.add(property.copy(info.getDevice_name()));
            }
        }
        device.setPropertys(lstproperty);

        //创建设备的信号
        List<IProductSignal> lstSignal = new ArrayList<IProductSignal>();
        lst_signal.forEach(e->
        {
            lstSignal.add(e.copy(info.getDevice_name()));
        });
        device.setSignals(lstSignal);

        //创建设备的数据采集器,并注册到产品容器中
        if (device.CreateGather())
        {
            IDeviceGather gather = device.getGather();
            if (null != gather)
            {
                ProductionContainer.getTheMeterDeviceContainer().RegisteGather(gather);
            }
        }

        return device;
    }

    @Override
    public boolean AddDevice(MyProductDeviceInfo info) {

        if (info == null)
        {
            return false;
        }

        if (getDevice(info.getDevice_name())!= null)
        {
            return false;
        }

       IDevice device =  CreateDevice(info);
        lst_device.add(device);

        //添加到映射表中
        ProductionContainer.getTheMeterDeviceContainer().AddDeviceToMap(device);

        return true;
    }

    @Override
    public List<IDevice> getAllDevice() {
        return lst_device;
    }

    @Override
    public IDevice getDevice(String deviceName) {

        List<IDevice> lst = new ArrayList<IDevice>();
        int nCount = lst_device.size();
        for(int i =0 ;i < nCount; i++)
        {
            IDevice device = lst_device.get(i);
            if (null != device && device.getDeviceName().equals(deviceName))
            {
                return device;
            }
        }

        return null;
    }

    public MyProduct(){

    }

    public MyProduct(MyProductInfo productInfo)
    {
        if(null == productInfo)
        {
            return;
        }

        product_name = productInfo.getProduct_name();
        product_dec = productInfo.getProduct_dec();
        product_class = productInfo.getProduct_class();
        product_key = productInfo.getProduct_key();
        product_type = productInfo.getProduct_type();
        product_protocal = productInfo.getProduct_protocal();
    }
}
