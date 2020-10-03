package com.xd.pre.modules.myeletric.mydb;

import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.mapper.MyCommandInfoMapper;
import com.xd.pre.modules.myeletric.mapper.MyProductDeviceMapper;
import com.xd.pre.modules.myeletric.service.IMyAreaService;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.modules.myeletric.service.IMyUserMeterService;
import com.xd.pre.modules.myeletric.service.impl.MyMeterServiceImpl;
import com.xd.pre.modules.pay.PaymentInfo;
import com.xd.pre.modules.pay.mapper.MyPaymentMapper;
import com.xd.pre.modules.sys.domain.SysUser;
import com.xd.pre.modules.sys.domain.SysUserCount;
import com.xd.pre.modules.sys.mapper.SysUserCountMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyDbStub {

    @Autowired
    private IMyMeterService iMyMeterService;               //电表

    @Autowired
    private IMyUserMeterService iMyUserMeterService;               //电表所有者映射关系


    @Autowired
    private MyCommandInfoMapper myCommandInfoMapper;        //命令

    @Autowired
    private IMyAreaService iMyAreaService;

    @Autowired
    private IMyRoomService iMyRoomService;

    @Autowired
    private SysUserCountMapper sysUserCountMapper;    //业主账户管理

    @Autowired
    private MyPaymentMapper ipaymentMapper ;     //支付对象

    @Autowired
    private MyProductDeviceMapper deviceMapper ;     //设备对象

    //单件对象
    public static MyDbStub sinTon = null;

    //获取单件对象
    public static MyDbStub getInstance()
    {
        if (null == sinTon)
        {
            sinTon = new MyDbStub();
        }

        return sinTon;
    }

    //设置Mapper
    public void setMapper(MyCommandInfoMapper devicecommandMapper,
                          IMyMeterService myMeterService ,
                          IMyAreaService areaService,
                          IMyRoomService roomService,
                          SysUserCountMapper userCountMapper,
                          MyPaymentMapper paymentMapper,
                          MyProductDeviceMapper devMapper,
                          IMyUserMeterService iMyUserMeterService1)
    {

        myCommandInfoMapper = devicecommandMapper;
        iMyMeterService = myMeterService;
        iMyAreaService =  areaService;
        iMyRoomService = roomService;
        sysUserCountMapper = userCountMapper;
        ipaymentMapper = paymentMapper;
        deviceMapper = devMapper;
        iMyUserMeterService = iMyUserMeterService1;
    }

    //保存命令对象
    public Integer saveDeviceCommandDB(MyCommandInfo commandInfo)
    {
        if (null == myCommandInfoMapper)
        {
            return 0;
        }

        return myCommandInfoMapper.saveCommandInfo(commandInfo);
    }

    //更新命令对象
    public  Integer uptDeviceCommand(MyCommandInfo commandInfo)
    {
        if (null == myCommandInfoMapper || commandInfo == null)
        {
            return 0;
        }

        return myCommandInfoMapper.updateCommandResult(commandInfo);
    }

    //提取最大命令号
    public  Integer getMaxCommandSN()
    {
        if (null == myCommandInfoMapper)
        {
            return 0;
        }
        try
        {
            return  myCommandInfoMapper.getMaxCommandSN();
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /****************************************************************************************************************************************
     *              电表管理
     *
     **********************************************************************************************************/
    public MyMeter getMeter(int nMeterID)
    {
        List<MyMeter> lstMeter = iMyMeterService.getMeter(nMeterID);
        if (null == lstMeter || lstMeter.size() == 0)
        {
            return null;
        }
        return lstMeter.get(0);
    }


    /****************************************************************************************************************************************
     *              园区管理
     *
     **********************************************************************************************************/
    public MyArea getArea(int nAreaID)
    {
        return iMyAreaService.getAreaByID(nAreaID);
    }

    /****************************************************************************************************************************************
     *              园房间管理
     *
     **********************************************************************************************************/
    public MyRoom getRoom(int nRoomID)
    {
        return iMyRoomService.getRoomByID(nRoomID);
    }

    /****************************************************************************************************************************************
     *              支付单
     *
     **********************************************************************************************************/
    public Integer SavePayment(PaymentInfo paymentInfo)
    {
        return ipaymentMapper.createNewPayment(paymentInfo);
    }

    /****************************************************************************************************************************************
     *              业主账号
     *
     **********************************************************************************************************/
    public SysUserCount GetUserCount(int nUserID)
    {
        List<SysUserCount> lstUserCount = sysUserCountMapper.getUserCountByUserID(nUserID);
        if (null == lstUserCount || lstUserCount.size() == 0)
        {
            return null;
        }

        return  lstUserCount.get(0);
    }

    /****************************************************************************************************************************************
     *              设备管理
     *
     **********************************************************************************************************/

    //提取最大设备编号,用于编号设备
    public  Integer getMaxDeviceNO()
    {
        if (null == myCommandInfoMapper)
        {
            return 0;
        }
        try
        {
            return  deviceMapper.getMaxDeviceNO();
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    //提取最大设备编号,用于编号设备
    public  Integer getSubDeviceNOByIMEI(String sIMEI)
    {
        if (null == deviceMapper)
        {
            return 0;
        }
        try
        {
            return  deviceMapper.getSubDeviceNOByIMEI(sIMEI);
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    //提取最大设备编号,用于编号设备
    public  List<MyProductDeviceInfo> getDeviceByGroup(String sGroup)
    {
        if (null == deviceMapper)
        {
            return null;
        }
        try
        {
            return  deviceMapper.getSubDeviceByGroup(sGroup);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    //添加一个硬件设备
    public Integer AddNewDevice(MyProductDeviceInfo deviceInfo)
    {
       try
       {
           return deviceMapper.addNewDevice(deviceInfo);
       }
       catch (Exception ex)
       {
            return 0;
       }
    }

    //添加一个逻辑电表
    public Integer AddNewMeter(MyMeter meter)
    {
        try
        {
            int ret = iMyMeterService.addNewMeter(meter);
            return ret ;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    //添加一个所有者映射关系
    public Integer AddNewMeterUser(MyUserMeter meteruser)
    {
        try
        {
            int ret = iMyUserMeterService.addNewUserMeter(meteruser);
            return ret ;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

}
