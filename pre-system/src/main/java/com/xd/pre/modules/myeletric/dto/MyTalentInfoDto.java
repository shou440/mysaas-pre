package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MyTalentInfoDto {

    /**
     * 租户基本信息
     */

    private Integer room_id;
    private Integer tenant_id;
    private String  user_id;
    private String  talent_name;
    private String  talent_tel;
    private String  talent_contactor;
}
