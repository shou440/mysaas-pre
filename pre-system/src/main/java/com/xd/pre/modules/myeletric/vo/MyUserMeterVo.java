package com.xd.pre.modules.myeletric.vo;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class MyUserMeterVo {

    /**
     * 用户购买电表映射表
     */

    private Integer  meter_id;
    private Integer  user_id;
    private Integer  pay_id;


}
