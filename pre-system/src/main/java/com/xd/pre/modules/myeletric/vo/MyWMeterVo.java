package com.xd.pre.modules.myeletric.vo;

import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class MyWMeterVo {

    /**
     * 电表数据（输出给Web端)
     */

    private Integer  meter_id;
    private Integer  room_id;
    private String   meter_sn;
    private String   room_name;
    private String   meter_type;
    private float    meter_scale;
    private Integer  meter_status;
    private String   meter_dec;
    private DateTime meter_crt_date;
    private DateTime meter_upt_date;
    private float    water_base;
    private float    water_last;
    private float    water_price;
    private  double  cur_water;           //当前水表读数
    private  Integer meter_signal;         //通讯信号
    private  Integer fresh_tick;    //数据刷新的tick

    public MyWMeterVo(){};

    public MyWMeterVo(MyWMeter meter)
    {
        meter_id = meter.getMeter_id();
        room_id = meter.getRoom_id();
        meter_sn = meter.getMeter_sn();
        meter_type = meter.getMeter_type();
        meter_scale = meter.getMeter_scale();
        meter_status = meter.getMeter_status();
        meter_dec = meter.getMeter_dec();
        meter_crt_date = meter.getMeter_crt_date();
        meter_upt_date = new DateTime();
        water_base = meter.getWater_base();
        water_last = meter.getWater_last();
        water_price = meter.getWater_price();
        cur_water = 0.0;
        meter_signal = 0;
        fresh_tick = 0;
    };

}
