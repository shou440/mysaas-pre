package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

/*
    电表下方的命令信息
 */
@Data
public class MeterCmdInfo {

    private String str_uuid = "";
    private int    int_uuid = 0;
    private int    cmd_code = 0;
    private int    cmd_state = 0;
    private int    expire_tick = 0;
    private int    last_tick = 0;
    private int    retry_gap = 0;
    private String    last_error = "";
}
