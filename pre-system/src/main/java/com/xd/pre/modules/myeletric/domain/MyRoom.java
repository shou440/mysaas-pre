package com.xd.pre.modules.myeletric.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
public class MyRoom {
    /**
     * 房间ID
      */

    private Integer room_id;
    private Integer area_id;
    private String room_name;
    private Integer room_status;
    private double tenant_fee;
    private double tenant_manage_fee;
    private double tenant_other_fee;
    private String tenant_name;
    private String  tenant_contactor;
    private String tenant_tel;
    private String tenant_openid;
    private LocalDateTime tenant_time;
    /*
        private String T_NAME;
    private String T_AGE;
    * */

}
