package com.xd.pre.modules.myeletric.device.gather;


import com.fasterxml.jackson.databind.ObjectReader;
import com.xd.MqttMsg;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.channel.ChannelContainer;
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProduct;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MyMqttMeterGather implements IDeviceGather {

    private final static int STATE_INIT       = 0x00;
    private final static int STATE_IDEL       = 0x01;
    private final static int STATE_INSERT     = 0x02;
    private final static int STATE_CALLDATA    = 0x03;

    private  int last_callall_tick = 0;
    private  int m_nRetryTimes = 0;
    private  int gather_state = STATE_INIT;
    private  String gather_name = "";
    private  DateTime time_last_callall = DateTime.now();

    private IMyMqttSubDevice cur_sub_device = null;

    //设备存根
    private IDevice meter_device = null;

    //当前命令
    private IMyCommand cur_command = null;

    //上一次发送的时间
    private int        last_send_tick = 0;

    //子设备列表
    private List<IMyMqttSubDevice> lst_subdevice = new ArrayList<IMyMqttSubDevice>();

    //数据轮询队列
    private LinkedBlockingQueue<IMyMqttSubDevice> queue_callall = new LinkedBlockingQueue<IMyMqttSubDevice>();

    //设备命令
    private LinkedBlockingQueue<IMyCommand> queue_command = new LinkedBlockingQueue<IMyCommand>();

    //挂新的子设备
    private LinkedBlockingQueue<IDevice> queue_new_subdevice = new LinkedBlockingQueue<IDevice>();

    //接收到的Mqtt数据
    private LinkedBlockingQueue<DeviceMqttMsg> queue_rec_msg = new LinkedBlockingQueue<DeviceMqttMsg>();

    public MyMqttMeterGather(IDevice device)
    {
        meter_device = device;

    }

    @Override
    public String getName() {
        return gather_name;
    }

    //获取数据采集器对应的通讯通道
    @Override
    public IMyChannel getChannel() {

        if (null == meter_device)
        {
            return null;
        }

        String sChannel = meter_device.getChannelName();
        IMyChannel channel = ChannelContainer.getChannelContainer().getChannel(sChannel);
        return channel;
    }

    //绑定通讯通道
    @Override
    public boolean bindChannel() {

        //将自己添加到通道中
        IMyChannel channel = getChannel();
        if (null != channel)
        {
            channel.addGather(this);
        }

        return false;
    }


    //通过子站号SubIndex获取子设备
    private IMyMqttSubDevice getSubDeviceByIndex(int nSubIndex)
    {
        IMyMqttSubDevice subDevice = null;

        for(int i = 0; i < lst_subdevice.size(); i++)
        {
            subDevice = lst_subdevice.get(i);
            IDevice device = subDevice.getDevice();
            if (null != subDevice && device.getSubIndex() == nSubIndex)
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
                cur_command = command;
                SwitchState(STATE_INSERT);
                return;
            }
        }

        int nGap = CommonFun.GetGap(last_callall_tick);
        //是否到达设备轮询时间,如果到达轮询的时间，则重新将子设备都加入轮询队列
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
                SwitchState(STATE_CALLDATA);
                last_send_tick = CommonFun.GetTick();
                last_callall_tick = CommonFun.GetTick();
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

            if(++m_nRetryTimes > 2 || queue_command.size() != 0)   //有插入命令则提前退出轮询
            {
                SwitchState(STATE_IDEL);
            }
            else
            {
                last_send_tick = CommonFun.GetTick();
                cur_sub_device.SendCallQuestCmd();
            }
        }

        //检查接收到的数据是否正确
        if (cur_sub_device.ProcessData(rec))
        {
            SwitchState(STATE_IDEL);
        }

    }

    //插入命令
    private void State_Insert()
    {

    }


    //处理突发命令
    private boolean ProcessNotify(byte[] data)
    {
        return  false;
    }


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
                        if (product.getProduct_name().equals("MY610-ENB"))
                        {
                            IMyMqttSubDevice subDevice = new My610ESubDevice(device,this);
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
        }

    }

    @Override
    public IDevice getDevice() {
        return meter_device;
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

        IDevice gateway = getDevice();
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
