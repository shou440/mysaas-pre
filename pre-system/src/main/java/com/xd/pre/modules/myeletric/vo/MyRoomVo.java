package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

@Data
public class MyRoomVo {

    private Integer room_id;
    private String room_inside_id;
    private String room_name;
    private String tenant_name;
    private String tenant_tel;
    private double ma_cost;
    private double re_cost;
    private double ot_cost;



}
