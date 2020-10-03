package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

@Data
public class CommandStateVO {
    private int command_sn;
    private int command_state;
    private String err_msg;
}
