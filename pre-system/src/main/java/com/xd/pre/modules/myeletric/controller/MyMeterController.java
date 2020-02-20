package com.xd.pre.modules.myeletric.controller;


import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
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



    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取房间的电表清单")
    @RequestMapping(value = "/getroommeters", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getRoomMeterList(MyMeterFilter filter) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyMeter> list=iMyMeterService.getMeterList(filter.getRoom_id());
        list.forEach(e->{

            MyMeterVo item = new MyMeterVo(e);

            String sKey = "MeterFee"+String.format("%06d",item.getMeter_id());
            if (redisTemplate.hasKey(sKey))
            {
                try
                {
                    String sValue= (String)redisTemplate.opsForHash().get(sKey,"TotalEP");
                    double dValue = Double.valueOf(sValue);
                    dValue /= 100.0f;
                    item.setM_cur_ep(dValue);

                    //数据刷新的Tick
                    String sTick = (String)redisTemplate.opsForHash().get(sKey,"FreshTick");
                    item.setMeter_fresh_tick(Integer.parseInt(sTick));
                }
                catch (Exception ex)
                {

                }
            }


            listOut.add(item);
        });


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

}
