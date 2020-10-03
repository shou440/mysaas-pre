package com.xd.pre.modules.sys.dto;

import lombok.Data;

@Data
public class SysUserCountDTO {

    private Integer user_id;
    private Integer  count_type;
    private String  user_count;
}
