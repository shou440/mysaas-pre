package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import com.xd.pre.modules.myeletric.mapper.MyAreaMapper;
import com.xd.pre.modules.myeletric.mapper.MyMeterFeeMapper;
import com.xd.pre.modules.myeletric.message.MyMessageContainer;
import com.xd.pre.modules.myeletric.message.MyPublicFeeMessage;
import com.xd.pre.modules.myeletric.service.IMyAreaService;
import com.xd.pre.modules.myeletric.service.IMyMeterFeeService;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.pay.PaymentInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyMeterFeeServiceImpl extends ServiceImpl<MyMeterFeeMapper, MyMeterFee> implements IMyMeterFeeService {

    @Autowired
    private IMyMeterService myMeterService;

    //获取园区电费
    @Override
    public List<MyMeterFee> getMeterFeeByArea(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getMeterFeeByArea(queryParam);
    }

    //获取房间的电费
    @Override
    public List<MyMeterFee> getMeterFeeByRoom(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getMeterFeeByRoom(queryParam);
    }



    //获取电表 的电费
    @Override
    public List<MyMeterFee> getMeterFeeByMeter(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getMeterFeeByMeter(queryParam);
    }

    //根据流水号查询订单
    @Override
    public MyMeterFee getMeterFeeBySn(String fee_sn)
    {
        List<MyMeterFee> lstFee = baseMapper.getMeterFeeBySn(fee_sn);
        if (null == lstFee || lstFee.size() == 0)
        {
            return null;
        }

        MyMeterFee fee = lstFee.get(0);

        return fee;
    }

    @Override
    public List<MyMeterFee> getMeterFeeByTenantOpenid(MyTenantFeeQueryDto queryParam) {
        return baseMapper.getMeterFeeByTenantOpenid(queryParam);
    }

    //生成电费的支付订单号
    @Override
    public Integer prePayMeterFee(@Param("param") MyMeterFeeUptParam param)
    {
        return  baseMapper.prePayMeterFee(param);
    }

    //更新电表费单的状态
    @Override
    public Integer updateStatus(MyMeterFeeUptParam item) {
        return baseMapper.updateStatus(item);
    }

    //批量更新电表的费单状态
    @Transactional
    @Override
    public Integer batchupdateStatus(List<MyMeterFeeUptParam> lst) {

        if (null == lst || lst.size() == 0)
        {
            return 0;
        }

        int len = lst.size();
        for(int i = 0; i < len; i++)
        {
            MyMeterFeeUptParam item = lst.get(i);
            if (null != item)
            {

                try
                {
                    int nRet =  baseMapper.updateStatus(item);
                    if (nRet != 1)
                    {
                        return 0;
                    }

                    //判断是否为发布费单,发送微信消息
                    if (item.getFee_status() == 1)
                    {
                        MyMeterFee meterFee = getMeterFeeBySn(item.getFee_sn());
                        if (null != meterFee)
                        {
                            System.out.print("发送电费单消息\n");
                            String sFee = String.format("%f",meterFee.getTotal_fee());
                            String tmStr = "2020-07-20";

                            MyPublicFeeMessage msg = new MyPublicFeeMessage(meterFee.getTenant_openid(),
                                    "水电费收费单",
                                    meterFee.getFee_sn(),
                                    meterFee.getRoom_name(),
                                    sFee,
                                    tmStr
                            );
                            MyMessageContainer.getSinTon().AddMessage(msg);


                        }

                    }
                }
                catch (Exception ex)
                {
                    return 0;
                }

            }
        }
        return 1;
    }

    //根据支付单的结果解锁收费单状态
    @Override
    public Integer unLockByPaymentResult(MyMeterFeeUptParam item) {
        return baseMapper.unLockByPaymentResult(item);
    }

    //批量更新电表的费单状态和支付单号
    @Transactional
    @Override
    public Integer batchupdatePayment(List<MyMeterFeeUptParam> lst) {

        if (null == lst || lst.size() == 0)
        {
            return 0;
        }

        int len = lst.size();
        for(int i = 0; i < len; i++)
        {
            MyMeterFeeUptParam item = lst.get(i);
            if (null != item)
            {

                try
                {
                    int nRet =  baseMapper.updatePayment(item);
                    if (nRet != 1)
                    {
                        return 0;
                    }



                }
                catch (Exception ex)
                {
                    return 0;
                }

            }
        }
        return 1;
    }


    //统计一段时间内的收费单，并保存到数据库种
    @Override
    public Integer countMeterFee(MyArea area, MyRoom room, MyMeter meter, MyRoomTenant tenant, MyMeterRdQryDto qrParam)
    {
        //判断是否为预付费的表，如果是则不生成计费单
        if (null == meter || ProductionContainer.getTheMeterDeviceContainer().IsPrechargeDevice(meter.getMeter_type()))
        {
            return 0;
        }

        List<MyMeterRecord> rdLst = myMeterService.getEpRecord(qrParam);
        MyMeterRecord minRd = null;
        MyMeterRecord maxRd = null;

        if (rdLst == null || rdLst.size() == 0)
        {
            return 0;
        }

        minRd = rdLst.get(0);
        int index = rdLst.size();
        if (index >= 1)
        {
            index -= 1;
        }
        maxRd = rdLst.get(index);

        //1、提取第一条记录 2、提取最后一条记录 3 统计总的电度
        float fTotalEp = 0;
        fTotalEp = maxRd.getEp_cur() - minRd.getEp_cur();
        if (fTotalEp < 0)
        {
            fTotalEp  = 0;
        }

        //记录结果,并保存到数据库
        String feeSN = String.format("%06d",meter.getMeter_id());
        Timestamp tmStamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime tm = tmStamp.toLocalDateTime();
        Month month = tm.getMonth();
        String sYear = String.format("%02d",tm.getYear()-2000);
        String sMonth = String.format("%02d",month.getValue());
        String sDay = String.format("%02d",tm.getDayOfMonth());
        String sHour = String.format("%02d",tm.getHour());
        String sMinute = String.format("%02d",tm.getMinute());
        String sSecond = String.format("%02d",tm.getSecond());
        feeSN = "E"+feeSN + sYear+sMonth+sDay+sHour+sMinute+sSecond;  //电费流水单前面加E


        MyMeterFee meterFee = new MyMeterFee();
        meterFee.setArea_id(area.getArea_id());
        meterFee.setArea_city(area.getArea_city());
        meterFee.setArea_name(area.getArea_name());
        meterFee.setRoom_id(room.getRoom_id());
        meterFee.setRoom_name(room.getRoom_name());
        meterFee.setMeter_id(meter.getMeter_id());
        meterFee.setUser_id(area.getUser_id());
        meterFee.setTenant_id(tenant.getRoom_tenant_id());
        meterFee.setFee_sn(feeSN);
        meterFee.setTenant_name(room.getTenant_name());
        meterFee.setTenant_openid(room.getTenant_openid());
        meterFee.setEp_start(minRd.getEp_cur());
        meterFee.setEp_end(maxRd.getEp_cur());
        meterFee.setTime_start(qrParam.getStart_time());
        meterFee.setTime_end(qrParam.getEnd_time());
        meterFee.setEp_used(fTotalEp);
        meterFee.setEp_price(meter.getEp_price());
        meterFee.setTotal_fee(meterFee.getEp_price()*meterFee.getEp_used());

        meterFee.setFee_status(0);                 //设置未支付标志
        meterFee.setPayment_id(0);
        meterFee.setTime_crt(new Timestamp(System.currentTimeMillis()));
        meterFee.setTime_upt(new Timestamp(System.currentTimeMillis()));

        return baseMapper.saveMeterFee(meterFee);
    }



}
