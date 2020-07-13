package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import com.xd.pre.modules.myeletric.vo.MyMeterFeeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IMyMeterFeeService  extends IService<MyMeterFee> {

    //获取园区电表的费用清单
    public List<MyMeterFee> getMeterFeeByArea(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //获取房间的电费清单
    public List<MyMeterFee> getMeterFeeByRoom(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    //获取电表的电费清单
    public List<MyMeterFee> getMeterFeeByMeter(@Param("queryParam") MyMeterFeeQueryDto queryParam);

    public MyMeterFee getMeterFeeBySn(String fee_sn);

    public List<MyMeterFee> getMeterFeeByTenantOpenid(@Param("queryParam") MyTenantFeeQueryDto queryParam);


    //生成电费的支付订单号
    public Integer prePayMeterFee(@Param("param") MyMeterFeeUptParam param);

    //修改电表费单的状态
    public Integer updateStatus(@Param("item") MyMeterFeeUptParam item);

    //更新费单的支付情况
    public Integer batchupdatePayment(List<MyMeterFeeUptParam> lst);

    //批量修改费单
    public Integer batchupdateStatus(List<MyMeterFeeUptParam> lst);

    //根据支付单的结果解锁费单
    public Integer unLockByPaymentResult(@Param("item") MyMeterFeeUptParam item);

    //保存收费单，并保存到数据库种(为了方便记录和提高效率，传入Area、room、Meter对象)
    public Integer countMeterFee(MyArea area, MyRoom room, MyMeter meter, MyRoomTenant tenant, MyMeterRdQryDto qrParam);
}
