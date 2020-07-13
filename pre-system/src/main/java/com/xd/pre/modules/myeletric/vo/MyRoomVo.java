package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyRoomVo {

    private Integer room_id;
    private Integer area_id;
    private String room_name;
    private Integer room_status;
    private double tenant_fee;
    private double tenant_manage_fee;
    private double tenant_other_fee;
    private String tenant_name;
    private String tenant_contactor;
    private String tenant_tel;
    private String tenant_openid;
    private LocalDateTime tenant_time;
}
