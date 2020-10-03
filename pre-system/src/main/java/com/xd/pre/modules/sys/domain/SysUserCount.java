package com.xd.pre.modules.sys.domain;

import lombok.Data;

//用户账号

@Data
public class SysUserCount {

    private Integer user_id;
    private Integer  count_type;
    private String  user_count;

}
