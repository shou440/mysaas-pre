package com.xd.pre.modules.myeletric.controller;


import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyWMeterBasePriceDto;
import com.xd.pre.modules.myeletric.service.*;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.myeletric.vo.MyWMeterVo;
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
@RequestMapping("/wmeter")
public class MyWMeterController {

    @Autowired
    private IMyWMeterService iMyWMeterService;

    @Autowired
    private IMyRoomService iRoomService;

    @Autowired
    private IMyUserWMeterService iMyUserWMeterService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //提取园区所有的水表
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取园区的水表清单")
    @RequestMapping(value = "/getareawmeters", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getAreaMeterList(MyMeterFilter filter) {


        //读取所有的水表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyWMeterVo> listOut = new ArrayList<MyWMeterVo>();
        List<MyRoom> lstRoom = iRoomService.getRoomInfo(filter.getArea_id());
        int nRoomCount = lstRoom.size();
        List<MyWMeter> list=iMyWMeterService.getAreaWMeterList(filter.getArea_id());

        for(int i = 0; i< list.size(); i++)
        {

            MyWMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }
            MyWMeterVo item = new MyWMeterVo(meter);
            for(int j = 0; j < nRoomCount;j++)
            {
                MyRoom room = lstRoom.get(j);
                if (null != room && room.getRoom_id() == meter.getRoom_id())
                {
                    item.setRoom_name(room.getRoom_name());
                }
            }



            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterWater(item);

            listOut.add(item);
        }

        return R.ok(listOut);
    }


    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取房间的水表清单")
    @RequestMapping(value = "/getroomwmeters", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getRoomMeterList(MyMeterFilter filter) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyWMeterVo> listOut = new ArrayList<MyWMeterVo>();
        List<MyWMeter> list=iMyWMeterService.getMeterList(filter.getRoom_id());
        for(int i = 0; i < list.size(); i++)
        {
            MyWMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }

            MyWMeterVo item = new MyWMeterVo(meter);

            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterWater(item);

            listOut.add(item);

        }

        return R.ok(listOut);
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "绑定水表")
    @RequestMapping(value = "/bindwmeter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R bindMeter(MyMeterFilter filter) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("权限验证错误，请重新登录!");
        }

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if(filter == null || filter.getRoom_id() == null)
        {
            return R.error("水表参数错误!");
        }

        //判断表是否存在
        List<MyWMeter> list=iMyWMeterService.getMeter(filter.getMeter_id());
        if(list.size() == 0)
        {
            return R.error("水表表号不存在!");
        }

        MyWMeter meter = list.get(0);
        if (meter == null)
        {
            return R.error("水表表号不存在!");
        }

        //判断电表是否已经开封,未开封购买不能使用
        if (meter.getMeter_status() == 0)
        {
            return R.error("水表未开封，无法绑定，请先扫水表上二维码购买开封!");
        }

        //检查该水表的是为该用户所有
        MyUserWMeter userWMeter = iMyUserWMeterService.getUserMeterByMeterid(filter.getMeter_id());
        if (null == userWMeter || userWMeter.getUser_id() != user.getUserId())
        {
            return R.error("该水表属于其他业主，请确认水表编号是否正确!");
        }

        //判断表是否已经被绑定
        if (meter.getRoom_id() != 0)
        {
            //查找绑定的房间名称
            MyRoom room = iRoomService.getRoomByID((meter.getRoom_id() ));
            if (null == room)
            {
                return R.error("该水表配置错误，请来联系0756-3332361 技术员解决!");
            }

            String sError = "该水表被房间:"+room.getRoom_name()+"绑定，请先解绑";
            return R.error(sError);
        }

        //绑定电表
        Integer ret = iMyWMeterService.bindMeter(filter);
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
    @SysOperaLog(descrption = "解除水表绑定")
    @RequestMapping(value = "/unbindwmeter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R unbindMeter(MyMeterFilter filter) {

        PreSecurityUser user = SecurityUtil.getUser();
        if (user == null) {
            return R.error("权限验证错误，请重新登录!");
        }

        //读取所有的水表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (filter == null || filter.getRoom_id() == null) {
            return R.error("表参数错误!");
        }

        //判断表是否存在
        List<MyWMeter> list = iMyWMeterService.getMeter(filter.getMeter_id());
        if (list.size() == 0) {
            return R.error("水表表号不存在!");
        }

        MyWMeter meter = list.get(0);
        if (meter == null) {
            return R.error("水表表号不存在!");
        }

        //判断该水表是否为该业主所属，不是则报告该水表业主无权限解除绑定
        MyUserWMeter userMeter = iMyUserWMeterService.getUserMeterByMeterid(filter.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId()) {
            return R.error("该水表属于其他业主，请确认水表编号是否正确!");
        }

        //解除水表绑定
        Integer ret = 0;
        try {

            ret = iMyWMeterService.unBindMeter(filter.getMeter_id());
        } catch (Exception ex) {
            String sErr = ex.getMessage();
            String fdf = "";
        }

        if (ret == 1) {
            //判断表释放存在
            return R.ok("解除绑定成功");
        } else {
            //判断表释放存在
            return R.error("解除绑定失败");
        }
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "设置水表期初值和水价")
    @RequestMapping(value = "/setwmeterbaseprice", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R setwmeterbaseprice(MyWMeterBasePriceDto wmeterbaseprice) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("权限验证错误，请重新登录!");
        }

        if (null == wmeterbaseprice)
        {
            return R.error("设置用水单价和期初值请求参数错误");
        }

        //判断该水表是否为该业主所属，不是则报告该水表业主无权设置电价
        MyUserWMeter userMeter = iMyUserWMeterService.getUserMeterByMeterid(wmeterbaseprice.getMeter_id());
        if (null == userMeter || userMeter.getUser_id() != user.getUserId())
        {
            return R.error("该水表属于其他业主，您无权设置!");
        }

        //修改电表的参数
        Integer ret = 0;
        try {

            ret = iMyWMeterService.updateWMeter(wmeterbaseprice);
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
