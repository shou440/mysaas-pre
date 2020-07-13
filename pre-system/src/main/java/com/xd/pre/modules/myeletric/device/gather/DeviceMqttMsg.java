package com.xd.pre.modules.myeletric.device.gather;

import java.util.ArrayList;
import java.util.List;

//设备接收的mqtt数据
public class DeviceMqttMsg {

    public List<String> lstTopicItem = new ArrayList<String>();
    public byte[] preload = null;

    public DeviceMqttMsg(List<String> lstTopicItem1,byte[] data)
    {
        lstTopicItem = lstTopicItem1;
        preload = data;
    }
}
