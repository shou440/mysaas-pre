package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

@Data
public class MyUserMeter {
    /**
     * 用户购买电表映射表
     */

    private Integer  meter_id;
    private Integer  user_id;
    private Integer  pay_id;

}
