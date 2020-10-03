package com.xd.pre.modules.myeletric.device.command;

public class MyCommandMessage {

    //消息类型
    public static final int  CMD_MSG_INVALID   = 0x00;
    public static final int  CMD_MSG_CMPLT     = 0x01;
    public static final int  CMD_MSG_ABORT     = 0x02;

    private int message_type = CMD_MSG_INVALID;
    private String err_msg = "";


    public MyCommandMessage(int msg,String errMsg)
    {
        message_type = msg;
        err_msg = errMsg;
    }

    public MyCommandMessage(int msg)
    {
        message_type = msg;
    }

    public int getMessage()
    {
        return message_type;
    }

    public String getErrMsg(){
        return err_msg;
    }


}
