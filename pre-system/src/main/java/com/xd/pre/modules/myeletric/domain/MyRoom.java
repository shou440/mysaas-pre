package com.xd.pre.modules.myeletric.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class MyRoom {
    /**
     * 房间ID
      */


    private Integer room_id;
    private String room_inside_id;
    private String room_name;
    private String tenant_name;
    private String tenant_tel;
    private double ma_cost;
    private double re_cost;
    private double ot_cost;

    /*
        private String T_NAME;
    private String T_AGE;
    * */




}
