package com.xd.pre.modules.myeletric.vo;

import com.xd.pre.modules.myeletric.domain.MyMeter;
import lombok.Data;
import org.joda.time.DateTime;

import java.sql.Timestamp;

@Data
public class MyMeterVo {

    /**
     * 电表数据（输出给Web端)
     */

    private Integer  meter_id;
    private Integer  room_id;
    private String   room_name;
    private String   product_name;
    private String   meter_type;
    private float    meter_ct;
    private float    meter_pt;
    private Integer  meter_status;
    private String   meter_dec;
    private DateTime meter_crt_date;
    private DateTime meter_upt_date;

    private float    ep_base;   //期初值
    private float    ep_last;   //上期读数
    private float    ep_price;  //电价
    private  float left_ep;              //剩余电度
    private  double cur_ep;              //当前读数
    private  Integer meter_signal;         //通讯信号
    private String meter_fresh_time_str;    //数据刷新的tick
    private Integer fresh_tick;

    public MyMeterVo(){};

    public MyMeterVo(MyMeter meter)
    {
        meter_id = meter.getMeter_id();
        room_id = meter.getRoom_id();
        meter_type = meter.getMeter_type();
        meter_ct = meter.getMeter_ct();
        meter_pt = meter.getMeter_pt();
        meter_status = meter.getMeter_status();
        meter_dec = meter.getMeter_dec();
        meter_crt_date = meter.getMeter_crt_date();
        meter_upt_date = new DateTime();
        ep_base = meter.getEp_base();
        ep_last = meter.getEp_last();
        ep_price = meter.getEp_price();
        cur_ep = 1.0;
        meter_signal = 0;
        meter_fresh_time_str= "";
        fresh_tick=0;
        left_ep = 0;
    };


}
