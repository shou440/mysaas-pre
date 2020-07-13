package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.domain.MyProductFunctionParamInfo;

public class MyProductFunctionParam {

    //参数类型
    public int data_type = 0;

    //参数名称
    public String param_name="";

    //参数输入输出方向
    public int param_direction=0;

    //参数值
    public int int_value=0;

    //浮点值
    public float float_value=0;

    //字符值
    public String str_value="";



    public MyProductFunctionParam()
    {

    }

    public MyProductFunctionParam(MyProductFunctionParamInfo info)
    {
        if (null == info)
        {
            return;
        }

        param_name = info.getParam_name();
        data_type = info.getParam_type();
        param_direction = info.getParam_direction();


    }


}
