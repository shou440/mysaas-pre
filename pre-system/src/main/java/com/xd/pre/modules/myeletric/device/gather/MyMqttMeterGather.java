package com.xd.pre.modules.myeletric.device.gather;


import com.fasterxml.jackson.databind.ObjectReader;
import com.xd.MqttMsg;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.channel.ChannelContainer;
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.command.MyCommandMessage;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProduct;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import org.aspectj.bridge.ICommand;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MyMqttMeterGather extends GatewayGather {

    private final static int STATE_INIT       = 0x00;
    private final static int STATE_IDEL       = 0x01;
    private final static int STATE_INSERT     = 0x02;
    private final static int STATE_CALLDATA    = 0x03;

    /*******************************************************************************************************************************************************
     *                 数据定义
     *
     *  1 gather_state,物联网关数据采集器状态机状态
     *  2 cur_sub_device,当前正在交互的网关子设备
     *  3 lst_subdevice,网关子设备的队列
     *  4 queue_callall,总召唤缓冲队列
     *  5 queue_rec_msg,数据接收缓冲
     *  6 time_last_callall,上一次轮询数据的时间
     ***************************************************************************************************************************************/
    protected  int      gather_state = STATE_INIT;
    protected  IMyMqttSubDevice cur_sub_device = null;
    protected  List<IMyMqttSubDevice> lst_subdevice = new ArrayList<IMyMqttSubDevice>();
    protected  LinkedBlockingQueue<IMyMqttSubDevice> queue_callall = new LinkedBlockingQueue<IMyMqttSubDevice>();
    protected  LinkedBlockingQueue<DeviceMqttMsg> queue_rec_msg = new LinkedBlockingQueue<DeviceMqttMsg>();

    public MyMqttMeterGather(IDevice device)
    {
        gateway_device = device;

    }

    //通过子站号SubIndex获取子设备
    private IMyMqttSubDevice getSubDeviceByDevNO(int nDevNO)
    {
        IMyMqttSubDevice subDevice = null;

        for(int i = 0; i < lst_subdevice.size(); i++)
        {
            subDevice = lst_subdevice.get(i);
            IDevice device = subDevice.getDevice();
            if (null != subDevice && device.getDevNO() == nDevNO)
            {
                return subDevice;
            }

        }

        return null;
    }

    //提取属性数据
    private boolean ProcessFreshProperty(DeviceMqttMsg msg)
    {
        if (null == msg || msg.lstTopicItem.size() < 5)
        {
            return false;
        }

        //检查是否为刷新属性的功能
        String sTopic = msg.lstTopicItem.get(4);
        if (sTopic == null || !sTopic.equals("FreshProperty"))
        {
            return false;
        }
        String sJson = new String(msg.preload);
        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject(sJson);
            JSONObject deviceListJson = jsonObject.getJSONObject("fresh_items");
            JSONArray deviceArrayJson = deviceListJson.getJSONArray("device_list");
            if (null != deviceArrayJson)
            {
                for(int i = 0; i < deviceArrayJson.length(); i++)
                {
                    JSONObject deviceJson = deviceArrayJson.getJSONObject(i);
                    if (null != deviceJson)
                    {
                        //提取设备名称
                        String sProductName = deviceJson.getString("product_name");
                        String sDeviceName = deviceJson.getString("device_name");

                        //提取属性列表
                        JSONArray propertyJsonArray = deviceJson.getJSONArray("property_list");
                        if (null != propertyJsonArray && propertyJsonArray.length() > 0)
                        {
                            for(int j = 0; j < propertyJsonArray.length(); j++)
                            {
                                JSONObject propertyJson = propertyJsonArray.getJSONObject(j);
                                if (null != propertyJson)
                                {
                                    String sPropertyName = propertyJson.getString("property_name");
                                    String sValue = propertyJson.getString("property_value");

                                    try
                                    {
                                        float fValue = Float.parseFloat(sValue);

                                        //查找设备
                                        IDevice device = GetSubDevice(sProductName,sDeviceName);
                                        if (null != device)
                                        {
                                            IProductProperty property =  device.getProperty(sPropertyName);
                                            if (null != property)
                                            {
                                                int nFloatBits= property.getFloatBits();
                                                for(int k = 0; k < nFloatBits;k++)
                                                {
                                                    fValue *= 10.0f;
                                                }
                                                property.setValue((int)fValue);
                                            }
                                        }

                                    }
                                    catch (Exception ex)
                                    {

                                    }

                                }
                            }
                        }
                    }
                }
            }

        }
        catch (Exception ex)
        {

        }

        return  true;

    }



    private void SwitchState(int nNextState)
    {

        gather_state = nNextState;

    }

    private void State_Init()
    {
        SwitchState(STATE_IDEL);
    }

    //空闲状态
    private  void State_Idel()
    {
        //判断是否有插入命令
        if (queue_command.size() != 0)
        {
            IMyCommand command = queue_command.poll();
            if (null != command)
            {
                IDevice device = command.getDevice();
                if (null != device)
                {
                    IMyMqttSubDevice subDevice =  getSubDeviceByDevNO(device.getDevNO());
                    if (null != subDevice)
                    {
                        cur_command = command;
                        cur_sub_device = subDevice;
                        last_send_tick = CommonFun.GetTick();
                        cur_sub_device.SendCommand(cur_command);

                        SwitchState(STATE_INSERT);

                        return;
                    }

                }
            }
        }

        //是否到达设备轮询时间,如果到达轮询的时间，则重新将子设备都加入轮询队列，5分钟一次
        int nGap = CommonFun.GetGap(last_callall_tick);
        if (nGap > 60 && queue_callall.size() == 0)
        {
            last_callall_tick = CommonFun.GetTick();

            queue_callall.clear();
            for (int i = 0; i < lst_subdevice.size(); i++)
            {
                IMyMqttSubDevice subDevice = lst_subdevice.get(i);
                if (null != subDevice)
                {
                    queue_callall.add(subDevice);
                }
            }

            return;
        }

        //提取轮询队列种一个设备进行查询
        if (queue_callall.size() > 0)
        {
            IMyMqttSubDevice subDevice = queue_callall.poll();
            if (null != subDevice)
            {
                cur_sub_device = subDevice;     //设置当前查询实时数据的设备，并重置次数和发送命令
                m_nRetryTimes = 0;
                cur_sub_device.SendCallQuestCmd();
                last_send_tick = CommonFun.GetTick();
                last_callall_tick = CommonFun.GetTick();

                SwitchState(STATE_CALLDATA);

            }
        }
    }

    //轮询设备
    private  void State_CallData(byte[] rec)
    {
        if (null == cur_sub_device)
        {
            SwitchState(STATE_IDEL);
            return;
        }

        //判断是否超时,超时则重发，等待10秒
        int nGap = CommonFun.GetGap(last_send_tick);
        if (nGap > 9)
        {

            SwitchState(STATE_IDEL);
        }

        //检查接收到的数据是否正确
        if (cur_sub_device.ProcessData(rec))
        {
            SwitchState(STATE_IDEL);
        }

    }




    //插入命令
    private void State_Insert(byte[] rec)
    {
        if (null == cur_sub_device)
        {
            SwitchState(STATE_IDEL);
            return;
        }

        //判断命令执行是否超时,超时则不执行
        if (cur_command.isExpired())
        {
            SwitchState(STATE_IDEL);
        }

        //判断是否超时,超时则重发，等待10秒
        int nGap = CommonFun.GetGap(last_send_tick);
        if (nGap > 9)
        {
            if(++m_nRetryTimes > 3)               //有插入命令则提前退出轮询
            {
                MyCommandMessage cmdMessage = new MyCommandMessage(MyCommandMessage.CMD_MSG_ABORT, "电表执行命令超时未响应");
                if (null != cur_command)
                {
                    cur_command.onMessage(cmdMessage);
                }

                SwitchState(STATE_IDEL);
            }
            else
            {
                last_send_tick = CommonFun.GetTick();
                cur_sub_device.SendCommand(cur_command);
            }
        }

        //检查接收到的数据是否正确,正确则发送消息给命令队列
        if (cur_sub_device.ProcessCommand(rec,cur_command))
        {
            MyCommandMessage cmdMessage = new MyCommandMessage(MyCommandMessage.CMD_MSG_CMPLT);
            if (null != cur_command)    //命令执行成功
            {
                cur_command.onMessage(cmdMessage);
            }

            SwitchState(STATE_IDEL);
        }
    }


    //处理突发命令
    private boolean ProcessNotify(byte[] data)
    {
        return  false;
    }


    //数据网关回调函数
    @Override
    public void callTick() {

        //判断是否添加了子设备
        if (queue_new_subdevice.size() > 0)
        {
            IDevice device = queue_new_subdevice.poll();

            if (null != device)
            {
                IProduct product = device.getProduct();
                if (null != product)
                {
                    IDevice deviceTmp = GetSubDevice(product.getProduct_name(),device.getDeviceName());
                    if (null == deviceTmp)
                    {
                        IMyMqttSubDevice subDevice = null;
                        if (product.getProduct_name().equals("MY610-ENB"))
                        {
                            subDevice = new My610ESubDevice(device,this);

                        }
                        else if (product.getProduct_name().equals("MY630-ENB"))
                        {
                            subDevice = new My630ESubDevice(device,this);

                        }

                        //添加到子设备队列
                        if (null != subDevice) {
                            lst_subdevice.add(subDevice);
                        }

                    }
                }

            }
        }

        //接收到的数据
        DeviceMqttMsg msg = null;
        byte[] data =null;
        if (queue_rec_msg.size() > 0)
        {
            msg = queue_rec_msg.poll();
            if (null != msg)
            {
                data = msg.preload;
                if (ProcessNotify(data))
                {
                    return;
                }
            }
        }

        //判断是否有子设备需要校时，如果需要则进行校时


        //检查是否有插入命令，如果有则插入命令
        switch (gather_state)
        {
            case STATE_INIT:
            {
                State_Init();
                break;
            }
            case STATE_IDEL:
            {
                State_Idel();
                break;
            }
            case  STATE_CALLDATA:
            {
                State_CallData(data);
                break;
            }
            case STATE_INSERT:
            {
                State_Insert(data);
                break;
            }
        }

    }



    //添加子设备
    @Override
    public void AddNewSubDevice(IDevice device) {

        if (null == device)
        {
            return;
        }
        try
        {
            queue_new_subdevice.put(device);
        }
        catch (Exception ex)
        {

        }

    }

    //获取子设备
    @Override
    public IDevice GetSubDevice(String productName, String sDeviceName) {

        int nCount = lst_subdevice.size();

        for(int i = 0; i < nCount; i++)
        {
            try
            {
                IMyMqttSubDevice subDevice  =lst_subdevice.get(i);
                IDevice device = subDevice.getDevice();
                if (null != device)
                {
                    IProduct product = device.getProduct();
                    if (null != device && null != product
                            && device.getDeviceName().equals(sDeviceName)
                            &&product.getProduct_name().equals(productName))
                    {
                        return device;
                    }
                }

            }
            catch (Exception ex)
            {
                return null;
            }
        }

        return null;
    }

    //接收到通道的数据
    @Override
    public void onReceive(Object data) {

        DeviceMqttMsg msg = (DeviceMqttMsg)data;
        if (null != msg)
        {
            try
            {
                queue_rec_msg.put(msg);
            }
            catch (Exception ex)
            {

            }

        }

    }

    //发送数据
    @Override
    public void SendData(Object data) {

        byte[] cmd = (byte[])data;
        if (null == cmd || cmd.length == 0)
        {
            return;
        }


        IDevice gateway = getGateWayDevice();
        if (null == gateway)
        {
            return;
        }


        IMyChannel channel = getChannel();
        if (null == channel)
        {
            return;
        }



        String sTopic = "/Gateway/Cmd/"+gateway.getDeviceName();
        MqttMsg msg = new MqttMsg(sTopic,cmd);
        channel.sendData(msg);

    }


    @Override
    public void onCommand(IMyCommand command) {

        try {
            queue_command.put(command);
        }
        catch (Exception ex)
        {

        }


    }
}
