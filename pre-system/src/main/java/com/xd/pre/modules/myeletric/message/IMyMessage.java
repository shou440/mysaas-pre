package com.xd.pre.modules.myeletric.message;

//系统消息接口

import java.sql.Timestamp;

public interface IMyMessage {

    Timestamp getTime();

    //设置消息体相关的对象
    void setMessageObj(Object msgObj);

    //获取消息相关的对象
    Object getMessageObj();

    //处理消息
    void ProcessMessage();

    //消息是否已经处理完成
    boolean hasProcessedCmplt();

}
