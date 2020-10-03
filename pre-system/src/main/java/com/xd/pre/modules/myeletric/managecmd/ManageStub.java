package com.xd.pre.modules.myeletric.managecmd;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xd.*;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import org.apache.ibatis.annotations.Param;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    系统维护单件，系统通过该单件，以mqtt为渠道，提供系统服务，包括资料维护等
 */
public class ManageStub {

    //单件对象
    private static ManageStub sinTon = null;
    private static final byte[] LOCK = new byte[0];

    private Thread thread_work = null;


    //获取单件对象
    public static ManageStub getSinTon()
    {
        if (null == sinTon)
        {
            sinTon = new ManageStub();
        }

        return sinTon;
    }

    //启动服务,订阅消息
    public void startService()
    {
        thread_work= new Thread(new ManageWorkThread());
        thread_work.start();
    }

    //返回执行结果给操作终端
    public void SendResult(String strCmd,int nRet,String  errMsg)
    {
        ManageCmdResult result = new ManageCmdResult(strCmd,nRet,errMsg);
        String sResult = result.toJsonString();
        if (sResult.length() > 0)
        {
            byte[] bytResult = sResult.getBytes(StandardCharsets.UTF_8);
            MyMqttStub.getTheMyMqttStub().publish("/server",bytResult);
        }

    }

    //创建物理设备和数据网关,并添加电表
    public void ProcessAddMeter(JSONObject meterCmdJson)
    {

        MyProductDeviceInfo  deviceInfo = new MyProductDeviceInfo();
        deviceInfo.setProduct_name(meterCmdJson.getString("product_name"));
        deviceInfo.setDevice_name(meterCmdJson.getString("device_name"));
        deviceInfo.setDevice_group(meterCmdJson.getString("device_group"));
        deviceInfo.setDevice_type(0);
        deviceInfo.setDevice_key(meterCmdJson.getString("device_key"));
        deviceInfo.setDevice_no(Integer.parseInt(meterCmdJson.getString("device_no")));
        deviceInfo.setGateway_name(meterCmdJson.getString("gateway_name"));
        deviceInfo.setGateway_channel(meterCmdJson.getString("gateway_channel"));
        deviceInfo.setDevice_memo(meterCmdJson.getString("device_memo"));
        deviceInfo.setCrt_time(new Timestamp(System.currentTimeMillis()));
        deviceInfo.setUpt_time(new Timestamp(System.currentTimeMillis()));

        //添加一个网关设备
        MyProductDeviceInfo  gatewayDeviceInfo = new MyProductDeviceInfo();
        gatewayDeviceInfo.setProduct_name("MYGW-100");
        gatewayDeviceInfo.setDevice_name(meterCmdJson.getString("gateway_name"));   //以网关的12位ID为设备名称
        gatewayDeviceInfo.setDevice_group(meterCmdJson.getString("device_group"));
        gatewayDeviceInfo.setDevice_type(1);                                             //设备名称为网关设备
        gatewayDeviceInfo.setDevice_key(meterCmdJson.getString("device_key"));
        gatewayDeviceInfo.setDevice_no(0);                                               //网关设备的设备号为0
        gatewayDeviceInfo.setGateway_name("");
        gatewayDeviceInfo.setGateway_channel(meterCmdJson.getString("gateway_channel"));   //网关通道
        gatewayDeviceInfo.setDevice_memo("网关"+meterCmdJson.getString("gateway_name"));
        gatewayDeviceInfo.setCrt_time(new Timestamp(System.currentTimeMillis()));
        gatewayDeviceInfo.setUpt_time(new Timestamp(System.currentTimeMillis()));

        //添加一个Meter
        MyMeter meter = new MyMeter();
        meter.setMeter_id(deviceInfo.getDevice_no());
        meter.setRoom_id(0);
        meter.setMeter_type(deviceInfo.getProduct_name());
        meter.setMeter_ct(1);
        meter.setMeter_pt(1);
        meter.setMeter_status(1);
        meter.setEp_base(0);
        meter.setEp_last(0);
        meter.setEp_price(1);
        meter.setMeter_dec(deviceInfo.getDevice_group());    //电表的备注默认设置分组号,这样容易整个转换给另外一个用户
        meter.setMeter_crt_date(new Timestamp(System.currentTimeMillis()));
        meter.setMeter_upt_date(new Timestamp(System.currentTimeMillis()));

        //添加一个电表用户的映射表
        MyUserMeter usermeter = new MyUserMeter();
        usermeter.setMeter_id(meter.getMeter_id());
        usermeter.setUser_id(8);
        usermeter.setPay_id("xianxia");
        usermeter.setCrt_time(new Timestamp(System.currentTimeMillis()));
        usermeter.setUpt_time(new Timestamp(System.currentTimeMillis()));


        if (1 == MyDbStub.getInstance().AddNewDevice(deviceInfo)
                && 1 == MyDbStub.getInstance().AddNewDevice(gatewayDeviceInfo)
           && 1 == MyDbStub.getInstance().AddNewMeter(meter)
          && 1== MyDbStub.getInstance().AddNewMeterUser(usermeter))   //添加一个子设备同时加一个网关设备
        {
            SendResult("AddMeter",ManageCmdResult.RESULT_SUCCESS,"");
        }
        else
        {
            SendResult("AddMeter",ManageCmdResult.RESULT_FAILED,"");
        }
    }

    //获取最大的设备编号
    public void GetMaxDeviceNO() {

        int NO = MyDbStub.getInstance().getMaxDeviceNO();
        String sNO = String.format("%d",NO);
        byte[] bytResult = sNO.getBytes(StandardCharsets.UTF_8);

        //提取最大设备编号
        MyMqttStub.getTheMyMqttStub().publish("/server",bytResult);
    }

    //通过网关获取子设备号
    public void getSubDeviceNOByIMEI(JSONObject meterCmdJson) {

        String sIMEI = meterCmdJson.getString("IMEI");

        int NO = MyDbStub.getInstance().getSubDeviceNOByIMEI(sIMEI);
        String sNO = String.format("%d",NO);


        //将结果返回成Json对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "GetSubDeviceNOByIMEI");
        jsonObject.put("deviceno", NO);
        String strJson=jsonObject.toString();
        byte[] bytResult = strJson.getBytes(StandardCharsets.UTF_8);

        MyMqttStub.getTheMyMqttStub().publish("/server",bytResult);
    }

    //通过分组获取所有的子设备
    public void getSubDeviceByGroup(JSONObject meterCmdJson) {

        String group = meterCmdJson.getString("group");
        List<MyProductDeviceInfo> lst = MyDbStub.getInstance().getDeviceByGroup(group);
        Map<String,Object> resultMap=new HashMap<String,Object>();
        List<Map> deviceMapLst=new ArrayList<>();


        //将结果返回成Json对象
        resultMap.put("cmd", "GetSubDeviceByGroup");
        if (null == lst)
        {
            resultMap.put("result", "0");

        }
        else
        {
            resultMap.put("result", "1");
            int nLen = lst.size();
            for(int i = 0; i < nLen; i++)
            {
                MyProductDeviceInfo deviceInfo = lst.get(i);
                if (null != deviceInfo)
                {
                    Map<String,Object> deviceMap=new HashMap<String,Object>();
                    deviceMap.put("product_name", deviceInfo.getProduct_name());
                    deviceMap.put("device_name", deviceInfo.getDevice_name());
                    deviceMap.put("device_group", deviceInfo.getDevice_group());
                    deviceMap.put("device_no", deviceInfo.getDevice_no());
                    deviceMap.put("gateway_name", deviceInfo.getGateway_name());
                    deviceMapLst.add(deviceMap);
                }
            }

            resultMap.put("device_list", deviceMapLst);
        }

        //将结果返回成Json对象
        JSONObject jsonObject = new JSONObject(resultMap);
        String strJson=jsonObject.toString();
        byte[] bytResult = strJson.getBytes(StandardCharsets.UTF_8);

        MyMqttStub.getTheMyMqttStub().publish("/server",bytResult);
    }

    //处理接收到的命令
    public void ProcessCmdQuest(byte[] data)
    {
        String sCmdStr = "";

        try
        {
            sCmdStr = new String(data, StandardCharsets.UTF_8);
            JSONObject cmdJson = JSON.parseObject(sCmdStr);
            if (null == cmdJson)
            {
                return;
            }

            //提取命令的类型
            String cmd = cmdJson.getString("cmd");
            if (cmd.equals("AddMeter"))                                  //处理添加电表命令
            {
                ProcessAddMeter(cmdJson);
            }
            else if (cmd.equals("GetMaxDeviceNO"))                        //获取最大的设备编号
            {
                GetMaxDeviceNO();
            }
            else if (cmd.equals("GetSubDeviceNOByIMEI"))                        //获取最大的设备编号
            {
                getSubDeviceNOByIMEI(cmdJson);
            }
            else if (cmd.equals("GetSubDeviceByGroup"))                //通过分组获取子设备
            {
                getSubDeviceByGroup(cmdJson);
            }



        }
        catch (Exception ex)
        {

        }

    }

    //工作线程
    public void Work()
    {
        while (true)
        {
            try{

                MqttMsg msg = MyMqttStub.getTheMyMqttStub().FetchRecMsg();
                if (null != msg)
                {
                    if (null != msg.preload && msg.preload.length != 0)
                    {
                        ProcessCmdQuest(msg.preload);
                    }
                }


                Thread.sleep(50);

            }catch (Exception ex)
            {

            }
        }
    }

}
