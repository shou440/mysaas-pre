package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyProductFunctionInfo {

    /**
     * 产品函数
     */

    private String  product_name;
    private String  function_name;           //函数名称
    private int     max_seconds ;            //超时的秒数
    private String  function_dec;            //函数描述
    private int     upt_tick;                //更新时间
}
