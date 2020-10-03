package com.xd.pre.modules.myeletric.controller;



import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.command.*;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.dto.*;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.modules.myeletric.service.IMyUserMeterService;
import com.xd.pre.modules.myeletric.vo.CommandSN;
import com.xd.pre.modules.myeletric.vo.CommandStateVO;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/meter")
public class MyMeterController {

    @Autowired
    private IMyMeterService iMyMeterService;

    @Autowired
    private IMyRoomService iRoomService;

    @Autowired
    private IMyUserMeterService iMyUserMeterService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //提取园区所有的电表
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取园区的电表清单")
    @RequestMapping(value = "/getareameters", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getAreaMeterList(MyMeterFilter filter) {


        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyRoom> lstRoom = iRoomService.getRoomInfo(filter.getArea_id());
        int nRoomCount = lstRoom.size();
        List<MyMeter> list=iMyMeterService.getAreaMeterList(filter.getArea_id());

        for(int i = 0; i< list.size(); i++)
        {

            MyMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }
            MyMeterVo item = new MyMeterVo(meter);
            for(int j = 0; j < nRoomCount;j++)
            {
                MyRoom room = lstRoom.get(j);
                if (null != room && room.getRoom_id() == meter.getRoom_id())
                {
                    item.setRoom_name(room.getRoom_name());
                }
            }

            String devName= "Meter"+String.format("%06d",meter.getMeter_id()) ;
            IDevice device = ProductionContainer.getTheMeterDeviceContainer().getDevice(devName);
            if (null != device)
            {
                item.setProduct_name(device.getProductName());
            }


            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);

            listOut.add(item);
        }

        return R.ok(listOut);
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取房间的电表清单")
    @RequestMapping(value = "/getroommeters", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getRoomMeterList(MyMeterFilter filter) {

        System.out.print("测试查询租户电表");
        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyMeter> list=iMyMeterService.getMeterList(filter.getRoom_id());
        for(int i = 0; i < list.size(); i++)
        {
            MyMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }

            MyMeterVo item = new MyMeterVo(meter);

            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);

            listOut.add(item);

        }

        return R.ok(listOut);
    }



    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "绑定电表")
    @RequestMapping(value = "/bindmeter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R bindMeter(MyMeterFilter filter) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("权限验证错误，请重新登录!");
        }

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if(filter == null || filter.getRoom_id() == null || filter.getRoom_id() == null)
        {
            return R.error("表参数错误!");
        }

        //判断表是否存在
        List<MyMeter> list=iMyMeterService.getMeter(filter.getMeter_id());
        if(list.size() == 0)
        {
            return R.error("电表表号不存在!");
        }

        MyMeter meter = list.get(0);
        if (meter == null)
        {
            return R.error("电表表号不存在!");
        }

        //判断电表是否已经开封,未开封购买不能使用
        if (meter.getMeter_status() == 0)
        {
            return R.error("电表未开封，无法绑定，请先扫电表上二维码购买开封!");
        }

        //检查该电表的是为该用户所有
        MyUserMeter userMeter = iMyUserMeterService.getUserMeterByMeterid(filter.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId())
        {
            return R.error("该电表属于其他业主，请确认电表编号是否正确!");
        }

        //判断表是否已经被绑定
        if (meter.getRoom_id() != 0)
        {
            //查找绑定的房间名称
            MyRoom room = iRoomService.getRoomByID((meter.getRoom_id() ));
            if (null == room)
            {
                return R.error("该电表配置错误，请来联系0756-3332361 技术员解决!");
            }

            String sError = "该电表被房间:"+room.getRoom_name()+"绑定，请先解绑";
            return R.error(sError);
        }

        //绑定电表
        Integer ret = iMyMeterService.bindMeter(filter);
        if (ret == 1)
        {
            //判断表释放存在
            return R.ok("绑定成功");
        }
        else
        {
            //判断表释放存在
            return R.error("绑定失败");
        }

    }


    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "解除电表绑定")
    @RequestMapping(value = "/unbindmeter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R unbindMeter(MyMeterFilter filter) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("权限验证错误，请重新登录!");
        }

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if(filter == null || filter.getRoom_id() == null)
        {
            return R.error("表参数错误!");
        }

        //判断表是否存在
        List<MyMeter> list=iMyMeterService.getMeter(filter.getMeter_id());
        if(list.size() == 0)
        {
            return R.error("电表表号不存在!");
        }

        MyMeter meter = list.get(0);
        if (meter == null)
        {
            return R.error("电表表号不存在!");
        }

        //判断该电表是否为该业主所属，不是则报告该电表业主无权限解除绑定
        MyUserMeter userMeter = iMyUserMeterService.getUserMeterByMeterid(filter.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId())
        {
            return R.error("该电表属于其他业主，请确认电表编号是否正确!");
        }

        //解除电表绑定
        Integer ret = 0;
        try {

            ret = iMyMeterService.unBindMeter(filter.getMeter_id());
        }
        catch (Exception ex)
        {
            String sErr = ex.getMessage();
            String fdf = "";
        }

        if (ret == 1)
        {
            //判断表释放存在
            return R.ok("解除绑定成功");
        }
        else
        {
            //判断表释放存在
            return R.error("解除绑定失败");
        }

    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "设置电表期初值和电价")
    @RequestMapping(value = "/setmeterbaseprice", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R setmeterbaseprice(MyMeterBasePriceDto meterbaseprice) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("权限验证错误，请重新登录!");
        }

        if (null == meterbaseprice)
        {
            return R.error("设置电表电价和期初值请求参数错误");
        }

        //判断该电表是否为该业主所属，不是则报告该电表业主无权设置电价
        MyUserMeter userMeter = iMyUserMeterService.getUserMeterByMeterid(meterbaseprice.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId())
        {
            return R.error("该电表属于其他业主，您无权设置!");
        }

        //修改电表的参数
        Integer ret = 0;
        try {

            ret = iMyMeterService.updateMeter(meterbaseprice);
        }
        catch (Exception ex)
        {
            String sErr = ex.getMessage();
            String fdf = "";
        }

        if (ret == 1)
        {
            //判断表释放存在
            return R.ok("设置成功");
        }
        else
        {
            //判断表释放存在
            return R.error("设置失败");
        }

    }

    /*获取新命令*/
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取命令流水号")
    @RequestMapping(value = "/getmetercommandsn", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getNewCommandSN() {

        System.out.print("获取新命令1111\n");
        CommandSN cmdSN = new CommandSN(CommandContainer.getInstance().getNewSN());
        return R.ok(cmdSN);



    }

    /*获取命令的执行结果*/
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取命令的执行结果")
    @RequestMapping(value = "/fetchmetercmdstate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R fetchMeterCommandState(MyCommandStateQrtDto cmdSN) {

        try {

            if(null == cmdSN)
            {
                System.out.print("获取命令传递参数为空");
            }

            String sState = "";
            String key = "commandstate_"+String.format("%012d",cmdSN.getCommand_sn());
            sState = MySystemRedisBuffer.getTheSinTon().getReisItemString(key);
            if (sState.equals(""))
            {
                return R.error("查询命令失败!");
            }
            int nState = Integer.parseInt(sState);

            String str = String.format("命令%s状态:%d\n",key,nState);
            System.out.print(str);

            //提取命令错误描述
            String errkey = "command-errmsg_"+String.format("%012d",cmdSN.getCommand_sn());
            String errmsg = MySystemRedisBuffer.getTheSinTon().getReisItemString(errkey);

            CommandStateVO cmdState = new CommandStateVO();
            cmdState.setCommand_sn(cmdSN.getCommand_sn());
            cmdState.setCommand_state(nState);
            cmdState.setErr_msg(errmsg);

            return R.ok(cmdState);
        }
        catch (Exception ex)
        {
            return R.error("获取命令状态异常!"+ex.getStackTrace());
        }


    }


    /*执行电表命令*/
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "执行电表交互命令")
    @RequestMapping(value = "/metercommand", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R meterCommand(MeterCommandDto meterCommandDto) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("您无权限操作电表，请重新登录!");
        }
        meterCommandDto.setUser_id(user.getUserId());

        if (null == meterCommandDto)
        {
            return R.error("电表执行命令参数错误");
        }

        //判断该电表是否为该业主所属，不是则报告该电表业主无权设置电价
        MyUserMeter userMeter = iMyUserMeterService.getUserMeterByMeterid(meterCommandDto.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId())
        {
            return R.error("该电表属于其他业主，您无权操作!");
        }


        //获取电表对象的硬件设备
        List<MyMeter> lstMeter = iMyMeterService.getMeter(meterCommandDto.getMeter_id());
        if (null == lstMeter || lstMeter.size() == 0)
        {
            return R.error("电表不存在!");
        }

        MyMeter meter = lstMeter.get(0);
        if (null ==meter)
        {
            return R.error("电表不存在!");
        }

        String sDeviceName = String.format("Meter%06d",meter.getMeter_id());
        IDevice meterDevice = ProductionContainer.getTheMeterDeviceContainer().getDevice(sDeviceName);
        if (null == meterDevice)
        {
            return R.error("电表配置数据错误!");
        }

        //执行电表命令
        try {

            MyCommandInfo cmd = new MyCommandInfo();
            IMyCommand command = CommandContainer.getInstance().CreateCommand(meterCommandDto);
            if (null == command)
            {
                return R.error("执行电表命令失败1!");
            }

            // 根据不同的命令初始化命令参数
            switch(command.getCommandType())
            {
                case IMyCommand.COMMAND_CHARGE:
                {
                    MyChargeCommand chargecommand = (MyChargeCommand)command;
                    if(null == chargecommand)
                    {
                        return R.error("充电命令参数错误!");
                    }
                    float fTotalEP = meterCommandDto.getCommand_value()/meter.getEp_price();
                    chargecommand.SetChargeFee(meterCommandDto.getCommand_value());
                    chargecommand.SetChargeEP(fTotalEP);
                    break;
                }
                case IMyCommand.COMMAND_MANNER_ADJUST:
                {
                    MyMeterDrawbackCommand drawbackCommand = (MyMeterDrawbackCommand)command;
                    if(null == drawbackCommand)
                    {
                        return R.error("退电命令参数错误!");
                    }
                    float fTotalEP = meterCommandDto.getCommand_value()/meter.getEp_price();
                    drawbackCommand.SetDrawbackFee(meterCommandDto.getCommand_value());
                    drawbackCommand.SetDrawbackEP(fTotalEP);
                    break;
                }
                case IMyCommand.COMMAND_MANNER_CLEARLEFT:
                {
                    MyClearLeftEPCommand clearLeftEPCommand = (MyClearLeftEPCommand)command;
                    if(null == clearLeftEPCommand)
                    {
                        return R.error("清零剩余电量参数错误!");
                    }

                    break;
                }
            }

          //  MyChargeCommand chargecommand = (MyChargeCommand)CommandContainer.getInstance().CreateCommand(meterCommandDto);
        //    if(null == chargecommand)
          //  {
         //       return R.error("电表操作命令参数错误!");
         //   }



            //设置电费
        //    float fTotalEP = meterCommandDto.getCommand_value()/meter.getEp_price();
          //  chargecommand.SetChargeFee(meterCommandDto.getCommand_value());
           // chargecommand.SetChargeEP(fTotalEP);



            //添加命令到命令缓冲队列
            CommandContainer.getInstance().AddNewCommand(command);

            return R.ok("");
        }
        catch (Exception ex)
        {
            return R.error("执行命令异常:"+ex.getMessage());
        }
    }

}
