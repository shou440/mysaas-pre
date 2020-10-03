package com.xd.pre.modules.myeletric.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.domain.MyWaterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeUptParam;
import com.xd.pre.modules.myeletric.mapper.MyMeterFeeMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterFeeService;
import com.xd.pre.modules.myeletric.service.IWaterFeeService;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fee")
public class MyFeeController {

    @Autowired
    private IMyMeterFeeService iMyMeterFeeService;

    @Autowired
    private IWaterFeeService iWaterFeeService;

    @Autowired
    private MyMeterFeeMapper MeterFeemapper;


    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "查询电费")
    @RequestMapping(value = "/getmeterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMeterFee(MyMeterFeeQueryDto queryParam) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (null == queryParam)
        {
            return R.error("电费查询输入参数错误!");
        }

        //查询电表
        List<MyMeterFee> meterFeeLst = null;
        if (queryParam.getMeter_id() != null && queryParam.getMeter_id() !=0 )
        {
            meterFeeLst  = iMyMeterFeeService.getMeterFeeByMeter(queryParam);
        }
        else if (queryParam.getRoom_id() != null && queryParam.getRoom_id() !=0 )
        {
            meterFeeLst  = iMyMeterFeeService.getMeterFeeByRoom(queryParam);
        }
        else if (queryParam.getArea_id() != null && queryParam.getArea_id() !=0 )
        {
            meterFeeLst  = iMyMeterFeeService.getMeterFeeByArea(queryParam);
        }
        else
        {
            return R.error("查询电费输入参数错误!");
        }
        return R.ok(meterFeeLst);
    }



    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "更新电费收费单的状态")
    @PostMapping(value = "/batchuptmeterfee")
    // @PostMapping(value = "/batchuptmeterfee", produces = "application/json;charset=UTF-8")
    public R updateMeterFee(@RequestBody String uptParam) {

        //检查房间是否属于用户的
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PreSecurityUser user = SecurityUtil.getUser();


        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (null == uptParam)
        {
            return R.error("更新电费单输入参数错误!");
        }

        int nNewState = 0;
        ArrayList<MyMeterFeeUptParam> lst = new ArrayList<MyMeterFeeUptParam>();
        try
        {
            JSONObject uptparmObj = JSON.parseObject(uptParam);
            if (null == uptparmObj)
            {
                return R.error("更新电费单数据错误!");
            }

            int nState = (int)uptparmObj.get("fee_state");
            JSONArray jArr = uptparmObj.getJSONArray("meterfee_info_lst");

            //检查是否有电表不属于该用户（从Redis中装载电表用户映射关系，检查该表是否属于该用户)
            for(int i = 0; i < jArr.size(); i++)
            {
                JSONObject jMeterInfoObj = jArr.getJSONObject(i);
                MyMeterFeeUptParam param =new MyMeterFeeUptParam();
                param.setFee_status((nState));
                param.setFee_sn(jMeterInfoObj.getString("fee_sn"));
                param.setMeter_id(jMeterInfoObj.getInteger("meter_id"));
                lst.add(param);
            }

            //批量修改参数
            iMyMeterFeeService.batchupdateStatus(lst);

        }
        catch (Exception ex)
        {
            return R.error("更新电费单数据格式错误!");
        }

        //查询电表
        return R.ok("电费费单更新成功!");
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "更新水费收费单的状态")
    @PostMapping(value = "/batchuptwmeterfee")
    // @PostMapping(value = "/batchuptwmeterfee", produces = "application/json;charset=UTF-8")
    public R updateWMeterFee(@RequestBody String uptParam) {

        //检查房间是否属于用户的
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PreSecurityUser user = SecurityUtil.getUser();


        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (null == uptParam)
        {
            return R.error("更新水费单输入参数错误!");
        }

        int nNewState = 0;
        ArrayList<MyMeterFeeUptParam> lst = new ArrayList<MyMeterFeeUptParam>();
        try
        {
            JSONObject uptparmObj = JSON.parseObject(uptParam);
            if (null == uptparmObj)
            {
                return R.error("更新水费单数据错误!");
            }

            int nState = (int)uptparmObj.get("fee_state");
            JSONArray jArr = uptparmObj.getJSONArray("meterfee_info_lst");

            //检查是否有电表不属于该用户（从Redis中装载电表用户映射关系，检查该表是否属于该用户)
            for(int i = 0; i < jArr.size(); i++)
            {
                JSONObject jMeterInfoObj = jArr.getJSONObject(i);
                MyMeterFeeUptParam param =new MyMeterFeeUptParam();
                param.setFee_status((nState));
                param.setFee_sn(jMeterInfoObj.getString("fee_sn"));
                param.setMeter_id(jMeterInfoObj.getInteger("meter_id"));
                lst.add(param);
            }

            //批量修改参数
            iWaterFeeService.batchupdateStatus(lst);

        }
        catch (Exception ex)
        {
            return R.error("更新水费单数据格式错误!");
        }

        //查询电表
        return R.ok("水费费单更新成功!");
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "查询水费")
    @RequestMapping(value = "/getwaterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getWaterFee(MyMeterFeeQueryDto queryParam) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (null == queryParam)
        {
            return R.error("水费查询输入参数错误!");
        }

        //查询水表
        List<MyWaterFee> waterFeeLst = null;
        if (queryParam.getMeter_id() != null && queryParam.getMeter_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByMeter(queryParam);
        }
        else if (queryParam.getRoom_id() != null && queryParam.getRoom_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByRoom(queryParam);
        }
        else if (queryParam.getArea_id() != null && queryParam.getArea_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByArea(queryParam);
        }
        else
        {
            return R.error("查询水费输入参数错误!");
        }
        return R.ok(waterFeeLst);
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "结算费单或者充值单")
    @RequestMapping(value = "/paypromotion", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R payPromotion(MyMeterFeeQueryDto queryParam) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        if (null == queryParam)
        {
            return R.error("水费查询输入参数错误!");
        }

        //查询水表
        List<MyWaterFee> waterFeeLst = null;
        if (queryParam.getMeter_id() != null && queryParam.getMeter_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByMeter(queryParam);
        }
        else if (queryParam.getRoom_id() != null && queryParam.getRoom_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByRoom(queryParam);
        }
        else if (queryParam.getArea_id() != null && queryParam.getArea_id() !=0 )
        {
            waterFeeLst  = iWaterFeeService.getWaterFeeByArea(queryParam);
        }
        else
        {
            return R.error("查询水费输入参数错误!");
        }
        return R.ok(waterFeeLst);
    }

}
