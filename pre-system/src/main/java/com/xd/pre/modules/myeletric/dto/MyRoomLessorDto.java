package com.xd.pre.modules.myeletric.dto;


import lombok.Data;

import java.sql.Timestamp;

//出租房间传递参数
@Data
public class MyRoomLessorDto {
    private Integer room_id;
    private Timestamp period_start_time;

}
