package com.xd.pre.modules.myeletric.domain;


import lombok.Data;

//用户命令数据记录
@Data
public class MyCommandInfo {

    //产品种类
    private String    product_name;
    private String    device_name;
    private Integer   command_sn;
    private Integer   user_id;
    private Integer   start_tick;
    private Integer   expire_tick;
    private Integer   end_tick;
    private Integer   result_code;
    private String    err_msg;
    private String    command_memo;
}
