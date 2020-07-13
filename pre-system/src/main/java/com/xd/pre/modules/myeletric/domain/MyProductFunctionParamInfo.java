package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyProductFunctionParamInfo {

    /**
     * 产品函数参数
     */

    private String  product_name;
    private String  function_name;           //函数名称
    private String  param_name ;             //参数名称
    private int     param_type;              //参数类型
    private String  param_dec;               //参数描述
    private int     param_direction;         //参数输入输出方向,0:输入，1输出
    private int     upt_tick;                //更新时间

}
