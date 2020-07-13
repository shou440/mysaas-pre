package com.xd.pre.modules.prepay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.prepay.domain.PreMeter;
import com.xd.pre.modules.prepay.dto.PreMeterDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PreMeterMapper extends BaseMapper<MyMeter> {

    //获取指定表号的预付费电表
    public List<PreMeter> getMeterByID(@Param("meterid") Integer meterid);

    //获取用户的电表列表
    public List<PreMeter> getMetersByOwner(@Param("openid") String openid);

    //获取租户的预付费电表列表
    public List<PreMeter> getMetersByTenant(@Param("openid") String openid);

    //指定电表的业主
    public Integer bindOwner(@Param("premeter") PreMeterDto premeter);

    //指定电表的租户
    public Integer bindTenant(@Param("premeter") PreMeterDto premeter);


}
