package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.domain.MyProductEventInfo;

public class MyProductEvent {

    //产品名称
    public String product_name="";

    //设备名称
    public String device_name = "";

    //事件名称
    public String event_name = "";

    //事件名称
    public String event_dec = "";

    //描述
    public int event_level=0;      //0:事件,1:告警,2:故障

    //发生的时间
    int happen_tick=0;

    public MyProductEvent()
    {

    }

    public MyProductEvent(MyProductEventInfo info)
    {
        if (null == info){
            return;
        }

        product_name = info.getProduct_name();
        device_name="";
        event_name = info.getEvent_name();
        event_dec = info.getEvent_dec();

    }

    public MyProductEvent copy(String sDeviceName)
    {
        MyProductEvent newEvent = new MyProductEvent();
        newEvent.product_name = product_name;
        newEvent.device_name = sDeviceName;
        newEvent.event_dec = event_dec;
        newEvent.event_name = event_name;
        newEvent.event_level = event_level;

        return  newEvent;
    }



}
