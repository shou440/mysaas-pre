package com.xd.pre.modules.myeletric.controller;


import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProduct;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyRoomFilterDto;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.modules.myeletric.service.IMyUserMeterService;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.sys.util.RedisUtil;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
