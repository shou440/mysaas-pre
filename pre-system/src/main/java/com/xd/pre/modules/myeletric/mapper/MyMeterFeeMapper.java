package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Repository
public interface MyMeterFeeMapper  extends BaseMapper<MyMeterFee> {

    //查询园区电费
    public List<MyMeterFee> getMeterFeeByArea(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //查询房间电费
    public List<MyMeterFee> getMeterFeeByRoom(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //查询电表电费
    public List<MyMeterFee> getMeterFeeByMeter(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //查询电表电费
    public List<MyMeterFee> getMeterFeeBySn(@Param("fee_sn") String fee_sn);

    //查询电表电费(通过租户的Openid)
    public List<MyMeterFee> getMeterFeeByTenantOpenid(@Param("queryParam") MyTenantFeeQueryDto queryParam);


    //生成电费的支付订单号
    public Integer prePayMeterFee(@Param("param") MyMeterFeeUptParam param);

    //生成新的收费单
    public Integer saveMeterFee(@Param("fee") MyMeterFee fee);

    //批量更新电费清单状态
    public Integer updateStatus(@Param("item") MyMeterFeeUptParam item);

    //根据支付单的结果更新费单状态
    public Integer unLockByPaymentResult(@Param("item") MyMeterFeeUptParam item);


    //更新支付单
    public Integer updatePayment(@Param("item") MyMeterFeeUptParam item);

}
