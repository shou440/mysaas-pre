package com.xd.pre.modules.myeletric.message;

import java.sql.Timestamp;

public class MyMessage implements IMyMessage {

    //消息内容
    protected Object message_obj = null;

    //发生的时间
    protected Timestamp time_crt = null;


    @Override
    public Timestamp getTime() {
        return time_crt;
    }

    @Override
    public void setMessageObj(Object msgObj) {

        message_obj = msgObj;

    }

    @Override
    public Object getMessageObj() {
        return message_obj;
    }

    @Override
    public void ProcessMessage() {

    }

    @Override
    public boolean hasProcessedCmplt() {
        return true;
    }
}
