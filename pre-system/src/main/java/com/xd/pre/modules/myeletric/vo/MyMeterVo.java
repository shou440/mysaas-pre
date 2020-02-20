package com.xd.pre.modules.myeletric.vo;

import com.xd.pre.modules.myeletric.domain.MyMeter;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class MyMeterVo {

    /**
     * 电表数据（输出给Web端)
     */

    private Integer  meter_id;
    private Integer  room_id;
    private String   meter_type;
    private float    meter_ct;
    private float    meter_pt;
    private Integer  meter_status;
    private String   meter_dec;
    private DateTime meter_crt_date;
    private DateTime meter_upt_date;

    private  double m_cur_ep;              //当前读数
    private  Integer meter_signal;         //通讯信号
    private  Integer meter_fresh_tick;    //数据刷新的tick

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
        m_cur_ep = 1.0;
        meter_signal = 0;
        meter_fresh_tick = 0;
    };


}
