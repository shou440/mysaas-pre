package com.xd.pre.modules.prepay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.prepay.domain.PreMeter;
import com.xd.pre.modules.prepay.dto.PreMeterDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IPreMeterService extends IService<PreMeter> {

    //获取指定表号的预付费电表
     List<PreMeter> getMeterByID(@Param("meterid") Integer meterid);

    //获取用户的电表列表
     List<PreMeter> getMetersByOwner(@Param("openid") String openid);

    //获取租户的预付费电表列表
     List<PreMeter> getMetersByTenant(@Param("openid") String openid);

    //指定电表的业主
     Integer bindOwner(@Param("premeter") PreMeterDto premeter);

    //指定电表的租户
     Integer bindTenant(@Param("premeter") PreMeterDto premeter);
}
