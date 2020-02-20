package com.xd.pre.modules.myeletric.vo;

import lombok.Data;

@Data
public class MyAreaVo {
    /**
     * 园区数据
     */

    private Integer area_id;
    private Integer user_id;
    private String  area_type;
    private String  area_city;
    private String  area_region;
    private String  area_addr;
    private String  area_name;
    private String  area_memo;
}
