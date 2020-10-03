package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyTenantFeeQueryDto;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyWaterFeeMapper;
import com.xd.pre.modules.myeletric.message.MyMessageContainer;
import com.xd.pre.modules.myeletric.message.MyPublicFeeMessage;
import com.xd.pre.modules.myeletric.service.IMyAreaService;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyWMeterService;
import com.xd.pre.modules.myeletric.service.IWaterFeeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
public class MyWaterFeeServiceImpl  extends ServiceImpl<MyWaterFeeMapper, MyWaterFee> implements IWaterFeeService {

    @Autowired
    private IMyWMeterService myWMeterService;

    //获取园区电费
    @Override
    public List<MyWaterFee> getWaterFeeByArea(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getWaterFeeByArea(queryParam);
    }

    //获取房间的电费
    @Override
    public List<MyWaterFee> getWaterFeeByRoom(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getWaterFeeByRoom(queryParam);
    }

    @Override
    public List<MyWaterFee> getWaterFeeByTenantOpenid(MyTenantFeeQueryDto queryParam) {
        return baseMapper.getWaterFeeByTenantOpenid(queryParam);
    }

    @Override
    public MyWaterFee getWaterFeeBySn(String fee_sn) {

        List<MyWaterFee> lstFee = baseMapper.getWaterFeeBySn(fee_sn);
        if (null == lstFee || lstFee.size() == 0)
        {
            return null;
        }

        MyWaterFee fee = lstFee.get(0);

        return fee;
    }


    //获取电表 的电费
    @Override
    public List<MyWaterFee> getWaterFeeByMeter(MyMeterFeeQueryDto queryParam)
    {
        return  baseMapper.getWaterFeeByMeter(queryParam);
    }

    //生成电费的支付订单号
    @Override
    public Integer prePayWaterFee(@Param("param") MyMeterFeeUptParam param)
    {
        return  baseMapper.prePayWaterFee(param);
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
                        MyWaterFee meterFee = getWaterFeeBySn(item.getFee_sn());
                        if (null != meterFee)
                        {
                            System.out.print("发送水费单消息\n");
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

    @Override
    public Integer unLockByPaymentResult(MyMeterFeeUptParam item) {
        return baseMapper.unLockByPaymentResult(item);
    }

    //统计一段时间内的收费单，并保存到数据库种
    @Override
    public Integer countWaterFee(MyArea area, MyRoom room, MyWMeter meter, MyRoomTenant tenant, MyMeterRdQryDto qrParam)
    {

        List<MyWMeterRecord> rdLst = myWMeterService.getWaterRecord(qrParam);
        MyWMeterRecord minRd = null;
        MyWMeterRecord maxRd = null;

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
        //1、提取第一条记录 2、提取最后一条记录 3 统计总的电度
        float fTotalWater = 0;
        fTotalWater = maxRd.getWater_cur() - minRd.getWater_cur();
        if (fTotalWater < 0)
        {
            fTotalWater  = 0;
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
        feeSN = "W"+feeSN + sYear+sMonth+sDay+sHour+sMinute+sSecond;

        MyWaterFee waterFee = new MyWaterFee();
        waterFee.setArea_id(area.getArea_id());
        waterFee.setArea_city(area.getArea_city());
        waterFee.setFee_sn(feeSN);
        waterFee.setArea_name(area.getArea_name());
        waterFee.setRoom_id(room.getRoom_id());
        waterFee.setRoom_name(room.getRoom_name());
        waterFee.setMeter_id(meter.getMeter_id());
        waterFee.setUser_id(area.getUser_id());
        waterFee.setTenant_id(tenant.getRoom_tenant_id());

        waterFee.setTenant_name(room.getTenant_name());
        waterFee.setTenant_openid(room.getTenant_openid());
        waterFee.setWater_start(minRd.getWater_last());
        waterFee.setWater_end(maxRd.getWater_cur());
        waterFee.setTime_start(qrParam.getStart_time());
        waterFee.setTime_end(qrParam.getEnd_time());
        waterFee.setWater_used(fTotalWater);
        waterFee.setWater_price(meter.getWater_price());
        waterFee.setTotal_fee(waterFee.getWater_price()*waterFee.getWater_used());

        waterFee.setFee_status(0);                 //设置未支付标志
        waterFee.setPayment_id("");
        waterFee.setTime_upt(new Timestamp(System.currentTimeMillis()));
        waterFee.setTime_crt(new Timestamp(System.currentTimeMillis()));


        return baseMapper.saveWaterFee(waterFee);
    }
}
