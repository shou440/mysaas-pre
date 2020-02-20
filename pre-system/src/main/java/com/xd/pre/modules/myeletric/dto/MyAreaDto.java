package com.xd.pre.modules.myeletric.dto;

import lombok.Data;

@Data
public class MyAreaDto {

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
