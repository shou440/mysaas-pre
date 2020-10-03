package com.xd.pre.modules.myeletric.managecmd;


import org.json.JSONObject;

//Mqtt管理软件与后台服务器的交互命令结果,用于返回给操作终端的命令结果
public class ManageCmdResult {

    public static final int  RESULT_SUCCESS = 1;
    public static final int  RESULT_FAILED = 0;

    public ManageCmdResult()
    {

    }

    public ManageCmdResult(String cmd,int ret, String err)
    {
        manage_cmd = cmd;
        cmd_result = ret;
        err_msg = err;
    }

    public String toJsonString()
    {
        String sJson = "";
        try
        {
            JSONObject object = new JSONObject();
            //string
            object.put("manage_cmd",manage_cmd);
            //int
            object.put("cmd_result",cmd_result);
            //boolean
            object.put("err_msg",err_msg);

            return  object.toString();

        }
        catch (Exception ex)
        {
            return "";
        }
    }

    //命令名称
    public  String  manage_cmd = "";

    //执行结果
    public int  cmd_result = RESULT_SUCCESS;

    //错误描述
    public String err_msg = "";
}
