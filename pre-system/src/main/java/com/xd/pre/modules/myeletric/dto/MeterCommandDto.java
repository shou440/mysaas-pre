package com.xd.pre.modules.myeletric.dto;

import lombok.Data;


//电表人工命令:充值、清零、调整电度、充值
@Data
public class MeterCommandDto {

    private int    command_sn;
    private int    meter_id;
    private int    command_id;
    private float  command_value;
    private int    user_id;
}
