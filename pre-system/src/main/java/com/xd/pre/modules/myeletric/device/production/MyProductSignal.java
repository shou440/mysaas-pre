package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.domain.MyProductSignalInfo;

public class MyProductSignal implements IProductSignal {

    //产品名称
    String product_name="";

    //设备名称
    String device_name = "";

    //属性名称
    String signal_name = "";

    //0值描述
    String value0_dec="";

    //1值描述
    String value1_dec="";

    //信号描述
    String signal_dec = "";

    //值
    int  vaule = 0;



    //数据刷新的时间戳
    int fresh_tick = 0;

    //数据刷新的时间戳
    int upt_tick = 0;

    @Override
    public String getProductName() {
        return product_name;
    }

    @Override
    public void setProductName(String productName) {
        product_name = productName;
    }

    @Override
    public String getDevName() {
        return device_name;
    }

    @Override
    public void SetDevName(String name) {
        device_name = name;
    }

    @Override
    public String getSignalName() {
        return signal_name;
    }

    @Override
    public void setSignalName(String sName) {
        signal_name = sName;
    }

    @Override
    public String getValue0Dec() {
        return value0_dec;
    }

    @Override
    public void setValue0Dec(String dec) {
        value0_dec = dec;
    }

    @Override
    public String getValue1Dec() {
        return value1_dec;
    }

    @Override
    public void setValue1Dec(String dec) {
        value0_dec = dec;
    }

    @Override
    public int getValue() {
        return vaule;
    }

    @Override
    public void setValue(int nValue) {
        vaule = nValue;
    }

    @Override
    public String getDec() {
        return signal_dec;
    }

    @Override
    public void setDec(String dec) {
        signal_dec = dec;
    }

    @Override
    public int getFreshTick() {
        return fresh_tick;
    }

    @Override
    public void setFreshTick(int nTick) {
        fresh_tick = nTick;
    }

    @Override
    public int getUptTick() {
        return upt_tick;
    }

    @Override
    public void setUptTick(int nTick) {
        upt_tick = nTick;
    }

    @Override
    public IProductSignal copy(String sDeviceName) {

        MyProductSignal signal = new MyProductSignal();
        signal.setProductName(product_name);
        signal.SetDevName(sDeviceName);
        signal.setSignalName(signal_name);
        signal.setValue0Dec(value0_dec);
        signal.setValue1Dec(value1_dec);
        signal.setDec(signal_dec);
        signal.setFreshTick(0);

        return  signal;
    }

    public MyProductSignal(){

    }

    public MyProductSignal(MyProductSignalInfo info)
    {
        if (null == info)
        {
            return;
        }

        product_name = info.getProduct_name();
        device_name = "";
        signal_name = info.getSignal_name();
        value0_dec = info.getValue0_dec();
        value1_dec = info.getValue1_dec();
        signal_dec = info.getSignal_dec();
        upt_tick = info.getUpt_tick();
        fresh_tick = 0;
    }
}
