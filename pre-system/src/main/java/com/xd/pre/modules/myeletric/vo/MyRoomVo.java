package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

@Data
public class MyRoomVo {

    private Integer room_id;
    private Integer area_id;
    private String room_name;
    private Integer room_status;
    private Integer tenant_id;
    private String tenant_name;
    private double tenant_fee;
    private double tenant_manage_fee;
    private double tenant_other_fee;


}
