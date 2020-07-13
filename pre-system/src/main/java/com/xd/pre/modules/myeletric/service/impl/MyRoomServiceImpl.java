package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.*;
import com.xd.pre.modules.myeletric.mapper.MyRoomTenantMapper;
import com.xd.pre.modules.myeletric.mapper.MyTenantHisMapper;
import com.xd.pre.modules.myeletric.service.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.List;

@Service
public class MyRoomServiceImpl extends ServiceImpl<MyRoomMapper,MyRoom> implements IMyRoomService {

    @Autowired
    private IMyAreaService myAreaService;

    @Autowired
    private MyRoomTenantMapper myRoomTenantMapper;

    @Autowired
    private MyTenantHisMapper myTenantHisMapper;

    @Autowired
    private IMyMeterFeeService myMeterFeeService;

    @Autowired
    private IWaterFeeService  myWaterFeeService;

    @Autowired
    private IMyMeterService myMeterService;

    @Autowired
    private IMyWMeterService myWMeterService;

    @Override
   public List<MyRoom> getRoomInfo(Integer areaid){

      List<MyRoom> list=baseMapper.getRoomInfo(areaid);
         list.forEach(e->{
             System.out.print(e.toString());
         });
        return  list;

    }

    @Override
    public  MyRoom getRoomByID(Integer roomid){

        List<MyRoom> list=baseMapper.getRoomByID(roomid);
        if (list == null || list.size() == 0)
        {
            return null;
        }
        MyRoom room = list.get(0);
        return  room;

    }


    @Override
    public int updateRoominfo(MyRoomDto room){

          baseMapper.updateRoominfo(room);

          return 0;
    }

    //创建房间同时创建租赁
    @Transactional
    @Override
    public MyRoomDto createNewRoom(MyRoomDto room){

        int ret = 0;
        baseMapper.createNewRoom(room);
        if (null != room)
        {
            ret =  createNewTenant(room.getRoom_id());
        }

        if (ret != 0)
        {
            return room;
        }
        else
        {
            return  room;
        }


    }

    @Override
    public int updateRoomtenant(MyRoomDto room){

        baseMapper.updateRoomtenant(room);
        return  0;

    }


    //获取房间出租信息
    @Override
    public MyRoomTenant getRoomLessor(Integer roomid)
    {
        List<MyRoomTenant> list=myRoomTenantMapper.getRoomLessor(roomid);
        if (list == null || list.size() == 0)
        {
            return null;
        }
        MyRoomTenant room = list.get(0);
        return  room;
    }

    //注册新房间租赁管理
    @Override
    public int createNewTenant(Integer roomid)
    {
        return  myRoomTenantMapper.createNew(roomid);
    }

    //出租房间
    @Transactional
    @Override
    public int LessorRoom(MyRoomLessorDto roomlessor)
    {
        //修改房间租赁状态
        if (roomlessor == null)
        {
            return 0;
        }

        //获取房间
        MyRoom room = getRoomByID(roomlessor.getRoom_id());
        if (null == room)
        {
            return 0;
        }
        if(room.getRoom_name() == null || room.getRoom_name() == "")
        {
            return 0;
        }


        //设置房间当前的租赁合同编号
        DateTime tm = new DateTime();
        String sTenantID = String.format("%06d",roomlessor.getRoom_id())    //根据房间号和当前生成合同流水编号
                    + String.format("%04d",tm.getYear())
                    + String.format("%02d",tm.getMonthOfYear()+1)
                    + String.format("%02d",tm.getDayOfMonth())
                    + String.format("%02d",tm.getHourOfDay())
                    + String.format("%02d",tm.getMinuteOfHour())
                    + String.format("%02d",tm.getSecondOfMinute());


        MyRoomTenantDto roomtenant = new MyRoomTenantDto();
        roomtenant.setRoom_id(roomlessor.getRoom_id());
        roomtenant.setRoom_tenant_id(sTenantID);
        Timestamp tmStart = roomlessor.getPeriod_start_time();
        tmStart.setHours(0);
        tmStart.setMinutes(0);
        tmStart.setSeconds(0);
        roomtenant.setPeriod_start_time(tmStart);

        try
        {
            if (0 == myRoomTenantMapper.LessorRoom(roomtenant))
            {
                return  0;
            }
        }
        catch (Exception ex)
        {
            return  0;
        }



        //修改租赁状态
        MyRoomDto roomdto = new MyRoomDto();
        roomdto.setRoom_id(roomlessor.getRoom_id());
        roomdto.setRoom_status(1);
        if (0 ==  baseMapper.updateRoomStatus(roomdto))
        {
            return 0;
        }



        //记录租赁历史
        MyTenantHisDto his = new MyTenantHisDto();
        his.setRoom_id(room.getRoom_id());
        his.setRoom_tenant_id(sTenantID);
        his.setRoom_name(room.getRoom_name());
        his.setTenant_name(room.getTenant_name());

        if (room.getTenant_tel() == null)
        {
            his.setTenant_tel("");
        }
        else
        {
            his.setTenant_tel(room.getTenant_tel());
        }

        if (room.getTenant_contactor() == null)
        {
            his.setTenant_contactor("");
        }
        else
        {
            his.setTenant_contactor(room.getTenant_contactor());
        }

        if (room.getTenant_openid() == null)
        {
            his.setTenant_openid("");
        }
        else
        {
            his.setTenant_openid(room.getTenant_openid());
        }
        his.setTenant_time(roomlessor.getPeriod_start_time());
        his.setCrt_time(new Timestamp(System.currentTimeMillis()));
        System.out.print("测试出租5");

        int ret =  myTenantHisMapper.saveTenantHis(his);

        return  ret;

    }

    //根据出租信息统计电费、水费、房间租赁费，并生成总收费单
    public  int CountRoomFee(MyRoomTenant tenant,Timestamp startTime,Timestamp endTime,String sErr)
    {
        return  CountRoomFee( tenant, startTime, endTime, sErr);


    }

    //房间退租
    @Transactional
    @Override
    public int CreateRoomBill(MyRoomTenant tenant,Timestamp startTime,Timestamp endTime,String sErr)
    {

        if (null == tenant)
        {
            sErr = "租赁信息错误!";
            return  0;
        }

        List<MyRoom> lst= baseMapper.getRoomByID(tenant.getRoom_id());
        if (null == lst || lst.size() == 0)
        {
            sErr = "获取房间信息失败";
            return  0;
        }
        MyRoom room = lst.get(0);

        if (room.getRoom_status() == 0)
        {
            sErr = "房间空置状态，无法退租";
            return  0;
        }

        //提取园区信息
        MyArea area = myAreaService.getAreaByID(room.getArea_id());
        if (null == area)
        {
            sErr = "获取园区数据错误";
            return  0;
        }

        //结算房间所有电表的电费
        List<MyMeter> lstMeter = myMeterService.getMeterList(tenant.getRoom_id());
        if (null != lstMeter && lstMeter.size() > 0)
        {
            lstMeter.forEach(meter -> {

                //获取所属期内最小的记录
                MyMeterRdQryDto queryParam = new MyMeterRdQryDto();
                queryParam.setMeter_id(meter.getMeter_id());
                queryParam.setStart_time(startTime);
                queryParam.setEnd_time(endTime);
                myMeterFeeService.countMeterFee(area,room,meter,tenant,queryParam);

            });
        }


        //结算房间所有水表的水费
        List<MyWMeter> lstWMeter = myWMeterService.getMeterList(tenant.getRoom_id());
        if (null != lstWMeter && lstWMeter.size() > 0)
        {
            lstWMeter.forEach(wmeter -> {

                //获取所属期内最小的记录
                MyMeterRdQryDto queryParam = new MyMeterRdQryDto();
                queryParam.setMeter_id(wmeter.getMeter_id());
                queryParam.setStart_time(startTime);
                queryParam.setEnd_time(endTime);
                myWaterFeeService.countWaterFee(area,room,wmeter,tenant,queryParam);

            });
        }

        //结算租金和管理费

        return 1;
    }

    //房间退租
    @Transactional
    @Override
    public int UnLessorRoom(MyRoomLessorDto roomLessorDto,String sErr)
    {
        //退租房间
        try
        {
            //检查房间是否存在
            if (null == roomLessorDto)
            {
                sErr = "房间参数错误";
                return  0;
            }

            List<MyRoom> lst= baseMapper.getRoomByID(roomLessorDto.getRoom_id());
            if (null == lst || lst.size() == 0)
            {
                sErr = "获取房间信息失败";
                return  0;
            }

            MyRoom room = lst.get(0);

            if (room.getRoom_status() == 0)
            {
                sErr = "房间空置状态，无法退租";
                return  0;
            }

            //获取房间出租信息对象
            List<MyRoomTenant> lstTenant = myRoomTenantMapper.getRoomLessor(room.getRoom_id());
            if (null == lstTenant || lstTenant.size() == 0)
            {
                sErr = "房间出租参数错误";
                return  0;
            }

            MyRoomTenant roomTenant = lstTenant.get(0);

            //以未结算为起点时间，结算到目前的时间
            Timestamp tmEnd = new Timestamp(System.currentTimeMillis());
            Timestamp tmStart = roomTenant.getPeriod_start_time();

            int ret = CreateRoomBill(roomTenant,tmStart,tmEnd,sErr);
            if (ret == 0)
            {
                sErr = "结算房费错误!";
                return  0 ;
            }

            //删除当期租赁信息
            ret = myRoomTenantMapper.ResetTenant(roomTenant.getRoom_id());
            if (ret == 0)
            {
                sErr = "退租失败!";
                return  0 ;
            }

            //设置房间为空置状态
            MyRoomDto roomDto= new MyRoomDto();
            roomDto.setRoom_id(roomTenant.getRoom_id());
            roomDto.setRoom_status(0);
            ret=  baseMapper.updateRoomStatus(roomDto);
            if (ret == 0)
            {
                sErr = "退租失败1!";
                return  0 ;
            }

            //修改租赁历史中租赁的结束时间
            MyTenantHisDto tenantHis = new MyTenantHisDto();
            tenantHis.setRoom_tenant_id(roomTenant.getRoom_tenant_id());
            tenantHis.setEnd_time(tmEnd);
            ret = myTenantHisMapper.updateTenantHis(tenantHis);

            return ret;

        }
        catch (Exception ex)
        {
            System.out.print("退租异常:"+ex.getMessage());
            return 0;
            //return R.error("修改异常");
        }


    }



}
