package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MyCommandInfoDto {

    private Integer command_sn;
    private Integer end_tick;
    private Integer result_code;
    private String  err_msg;
}
