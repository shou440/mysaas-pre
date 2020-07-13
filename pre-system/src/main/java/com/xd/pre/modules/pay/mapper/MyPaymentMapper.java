package com.xd.pre.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.pay.PaymentInfo;
import com.xd.pre.modules.pay.dto.PaymentUptParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyPaymentMapper extends BaseMapper<PaymentInfo> {

    //获取所有锁定的支付订单
    public List<PaymentInfo> getLockedPayment();

    //获取租户的支付历史记录
    public List<PaymentInfo> getPaymentByTenant(@Param("openid") String openid);

    //根据支付单号获取支付单信息
    public List<PaymentInfo> getPaymentByID(@Param("paymentid") String paymentid);

    //更新支付单的状态
    public Integer updatePaymentStatus(@Param("payparam") PaymentUptParam payparam);

    //创建新的支付单
    public Integer createNewPayment(@Param("pay") PaymentInfo pay);

}
