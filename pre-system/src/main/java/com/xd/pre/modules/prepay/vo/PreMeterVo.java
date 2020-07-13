package com.xd.pre.modules.prepay.vo;

import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.prepay.domain.PreMeter;
import lombok.Data;
import org.joda.time.DateTime;

import java.sql.Timestamp;

@Data
public class PreMeterVo {

    /**
     * 预付费电表数据
     */

    private Integer  meter_id;
    private String   meter_type;
    private String   meter_sn;
    private String   meter_addr;
    private String   room_name;
    private String   owner_openid;
    private String   owner_tel;
    private String   manange_openid;
    private String   manage_tel;
    private String   tenant_openid;
    private String   tenant_tel;
    private String   tenant1_openid;
    private String   tenant1_tel;
    private float    ep_price;
    private float    ep_scale;
    private Timestamp meter_upt_date;
    private String   memo;

    //信号强度
    private Integer meter_signal;
    private String  fresh_time;

    //电量
    private float    ep_total;
    private float    ep_left;

    public PreMeterVo(){};

    public PreMeterVo(PreMeter meter)
    {
        meter_id = meter.getMeter_id();
        meter_type = meter.getMeter_type();
        meter_sn = meter.getMeter_sn();
        meter_addr = meter.getMeter_addr();
        room_name = meter.getRoom_name();
        owner_openid = meter.getOwner_openid();
        owner_tel = meter.getOwner_tel();
        manange_openid = meter.getManange_openid();
        manage_tel = meter.getManage_tel();
        tenant_openid = meter.getTenant_openid();
        tenant_tel = meter.getTenant_tel();
        tenant1_openid = meter.getTenant1_openid();
        tenant_tel = meter.getTenant1_tel();
        ep_price = meter.getEp_price();
        ep_scale = meter.getEp_scale();
        memo = meter.getMemo();
        ep_total = 0.0f;
        ep_left = 0.0f;
    };
}
