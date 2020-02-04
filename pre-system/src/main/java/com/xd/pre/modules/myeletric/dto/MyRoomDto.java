package com.xd.pre.modules.myeletric.dto;


import lombok.Data;

@Data
public class MyRoomDto {
    private Integer room_id;
    private String room_inside_id;
    private String room_name;
    private String tenant_id;
    private String tenant_name;
    private String tenant_tel;
    private Integer manager_id;
    private double ma_cost;
    private double re_cost;
    private double ot_cost;
}
