package com.xd;


public class MqttMsg {

    public MqttMsg(){

    }
    public MqttMsg(String topic1,byte[] preload1)
    {
        topic= topic1;
        preload = preload1;
    }

    public byte[] preload = null;
    public String topic = "";
}
