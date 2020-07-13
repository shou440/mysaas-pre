package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.domain.MyWaterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyWaterFeeMapper extends BaseMapper<MyWaterFee> {

    //查询园区水费
    public List<MyWaterFee> getWaterFeeByArea(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //查询房间水费
    public List<MyWaterFee> getWaterFeeByRoom(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //查询电表水费
    public List<MyWaterFee> getWaterFeeByMeter(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //根据流水号获取水费单
    public List<MyWaterFee> getWaterFeeBySn(String fee_sn);

    //查询电表电费(通过租户的Openid)
    public List<MyWaterFee> getWaterFeeByTenantOpenid(@Param("queryParam") MyTenantFeeQueryDto queryParam);


    //生成电费的支付订单号
    public Integer prePayWaterFee(@Param("param") MyMeterFeeUptParam param);

    //批量更新电费清单状态
    public Integer updateStatus(@Param("item") MyMeterFeeUptParam item);

    //生成新的收费单
    public Integer saveWaterFee(@Param("fee") MyWaterFee fee);

    //根据支付单的结果更新费单状态
    public Integer unLockByPaymentResult(@Param("item") MyMeterFeeUptParam item);

    //更新支付单
    public Integer updatePayment(@Param("item") MyMeterFeeUptParam item);

}
