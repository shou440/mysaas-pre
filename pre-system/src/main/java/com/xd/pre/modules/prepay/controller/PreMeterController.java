package com.xd.pre.modules.prepay.controller;

import com.alibaba.fastjson.JSON;
import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.myeletric.dto.MyWXUserFilterDto;
import com.xd.pre.modules.myeletric.vo.MyWMeterVo;
import com.xd.pre.modules.prepay.domain.PreMeter;
import com.xd.pre.modules.prepay.dto.PreMeterDto;
import com.xd.pre.modules.prepay.mapper.PreMeterMapper;
import com.xd.pre.modules.prepay.vo.PreMeterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.weixin4j.model.user.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/premeter")
public class PreMeterController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PreMeterMapper preMeterMapper;


    //获取指定表号的预付费电表
    @RequestMapping(value = "/getpremeterbyid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfo(PreMeterDto filter) {

        //获取当前连接的用户ID
        List<PreMeterVo> listOut = new ArrayList<PreMeterVo>();
        try {
            List<PreMeter> lst = preMeterMapper.getMeterByID(filter.getMeter_id());
            lst.forEach(premeter->{

                if (null == premeter){
                    return;
                }

                PreMeterVo meterVo1 = new PreMeterVo(premeter);

                //读取电表实时数据
                String sKey = "MeterEP"+String.format("%06d",premeter.getMeter_id());
                if (redisTemplate.hasKey(sKey))
                {
                    try
                    {
                        String sValue= (String)redisTemplate.opsForHash().get(sKey,"TotalEP");
                        float dValue = Float.valueOf(sValue);
                        dValue /= 100.0f;
                        meterVo1.setEp_total(dValue);

                        //信号强度
                        sValue= (String)redisTemplate.opsForHash().get(sKey,"Signal");
                        meterVo1.setMeter_signal(Integer.parseInt(sValue));

                        //数据刷新的Tick
                        String sTick = (String)redisTemplate.opsForHash().get(sKey,"Tick");
                        meterVo1.setFresh_time(sTick);
                    }
                    catch (Exception ex)
                    {

                    }
                }

                listOut.add(meterVo1);

                return ;
            });
        } catch (Exception ex) {

            return R.error("获取用户电表信息失败");
        }

        if (listOut.size() == 0)
        {
            return R.error("获取预付费电表数据失败");
        }
        else
        {
            return R.ok(listOut.get(0));
        }
    }

    //设置电表的业主
    @RequestMapping(value = "/registepremeter", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R registePreMeter(PreMeterDto filter) {

        //获取电表信息
        List<PreMeterVo> listOut = new ArrayList<PreMeterVo>();
        try {
            PreMeter meter = null;
            List<PreMeter> lst = preMeterMapper.getMeterByID(filter.getMeter_id());
            if (lst.size() != 0)
            {
                meter = lst.get(0);
            }
            else
            {
                return R.error("电表表号不存在");
            }

            if (null == meter)
            {
                return R.error("电表表号不存在");
            }

            //检查电表是否已经有业主
            if (meter.getOwner_openid() != null && meter.getOwner_openid().length() > 3)
            {
                return R.error("该电表已经开封,无法重复开封!");
            }

            //指定业主
            if (filter.getOwner_openid() == "")
            {
                return R.error("业主微信号为空！");
            }

            //检查电表的电价是否正确
            if (filter.getEp_price() == 0)
            {
                return R.error("请指定电价！");
            }

            //检查电表的房号
            if (filter.getRoom_name() == "")
            {
                return R.error("请指定电表房号！");
            }

            //检查电表的地址
            if (filter.getMeter_addr() == "")
            {
                return R.error("请指定电表的地址！");
            }

            //绑定业主
            int nRet= preMeterMapper.bindOwner(filter);
            if(nRet == 1)
            {
                return R.ok("电表开封成功!");
            }




        } catch (Exception ex) {

            return R.error("获取用户电表信息失败");
        }

        return R.ok("电表开封失败!");
    }
}
