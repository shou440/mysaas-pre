package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IWaterFeeService  extends IService<MyWaterFee> {

    //获取园区的水费清单
    public List<MyWaterFee> getWaterFeeByArea(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //获取房间的电费清单
    public List<MyWaterFee> getWaterFeeByRoom(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    public List<MyWaterFee> getWaterFeeByTenantOpenid(@Param("queryParam") MyTenantFeeQueryDto queryParam);

    //根据流水号获取水费单
    public MyWaterFee getWaterFeeBySn(String fee_sn);


    //获取电表的电费清单
    public List<MyWaterFee> getWaterFeeByMeter(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //生成电费的支付订单号
    public Integer prePayWaterFee(@Param("param") MyMeterFeeUptParam param);

    //批量修改费单
    public Integer batchupdateStatus(List<MyMeterFeeUptParam> lst);

    //更新费单的支付情况
    public Integer batchupdatePayment(List<MyMeterFeeUptParam> lst);

    public Integer unLockByPaymentResult(@Param("item") MyMeterFeeUptParam item);

    //保存收费单，并保存到数据库种(为了方便记录和提高效率，传入Area、room、Meter对象)
    public Integer countWaterFee(MyArea area, MyRoom room, MyWMeter meter, MyRoomTenant tenant, MyMeterRdQryDto qrParam);

}
