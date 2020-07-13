package com.xd.pre.modules.myeletric.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//批量审核电费单参数

@Data
public class MyBatchUptMeterFeeDto {

    private int fee_state;

    private List<String>  fee_sn_lst;



}
