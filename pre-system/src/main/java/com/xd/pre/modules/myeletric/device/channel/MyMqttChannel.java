package com.xd.pre.modules.myeletric.device.channel;

import com.xd.MqttMsg;
import com.xd.pre.modules.myeletric.device.channel.filter.IChannelFilter;
import com.xd.pre.modules.myeletric.device.gather.DeviceMqttMsg;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.domain.MyChannelMqttInfo;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class MyMqttChannel  implements  IMyChannel{

    //通道号
    private int channel_id = 0;

    //通道名称
    private String channelName = "";

    //服务器
    private MyChannelMqttInfo mqtt_info = null;

    private String SERVER_URL = "tcp://134.175.52.44:6200";
    private String clientid  = "client4";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "admin";
    private String passWord = "password";
    private ScheduledExecutorService scheduler;
    private List<String> lstTopic = new ArrayList<String>();
    private List<IDeviceGather> lstGather = new ArrayList<IDeviceGather>();

    private boolean is_working = false;
    private boolean is_need_ReSubcribe = false;          //是否需要重新订阅，用于重新连接后订阅消息

    //任务线程
    private ChannelSendThread thread_send= null;
    private ChannelRecThread thread_rec = null;

    //已经启动的标志位
    private boolean has_started  = false;

    //数据发送队列缓冲
    private  LinkedBlockingQueue<MqttMsg> queue_send = new LinkedBlockingQueue<MqttMsg>();

    //数据接收队列缓冲
    private  LinkedBlockingQueue<MqttMsg> queue_rec = new LinkedBlockingQueue<MqttMsg>();

    //新挂接到通道下的设备数据采集器
    private  LinkedBlockingQueue<IDeviceGather> queue_newGather = new LinkedBlockingQueue<IDeviceGather>();

    //默认构造函数
    public MyMqttChannel(){

    }

    //带信息的构造函数
    public MyMqttChannel(MyChannelMqttInfo info)
    {
        mqtt_info = info;
        if (null != info)
        {
            channelName = info.getChannel_name();
            channel_id = info.getChannel_id();
        }
    }

    //判断是否已经启动
    public boolean hasStarted()
    {
        return  has_started;
    }

    @Override
    public int getChannelID() {
        return channel_id;
    }

    @Override
    public void setName(String Name) {
        channelName = Name;
    }

    @Override
    public String getName() {
        return channelName;
    }


    //断开连接事件
    public void OnDisconnected()
    {
        String sData = "";
    }

    //重新连接事件回调
    public void OnReconnected()
    {
        is_need_ReSubcribe = true;
    }

    //发送数据
    @Override
    public void sendData(Object data) {

        MqttMsg msg = (MqttMsg)data;
        if (null != data)
        {
            try
            {
                queue_send.put(msg);
            }
            catch (Exception ex)
            {

            }
        }

    }

    @Override
    public void OnReceive(Object data) {

        MqttMsg msg = (MqttMsg)data;
        if (null != msg)
        {
            try
            {
                queue_rec.put(msg);
            }
            catch (Exception ex)
            {

            }

        }
    }

    //订阅Gather的数据
    private void SubcribeGather(IDeviceGather gather)
    {
        if (null != gather)
        {
            //添加设备的订阅
            try
            {
                IDevice device = gather.getGateWayDevice();
                String sTopic = "/Gateway/Response/"
                        +device.getDeviceName();

                client.subscribe(sTopic);
            }
            catch (Exception ex)
            {

            }
        }
    }

    //取消Gather的订阅
    private void UnsubcribeGather(IDeviceGather gather)
    {
        if (null != gather)
        {
            //添加设备的订阅
            try
            {
                IDevice device = gather.getGateWayDevice();
                String sTopic = "/Gateway/Response/"
                        +device.getDeviceName();

                client.unsubscribe(sTopic);
            }
            catch (Exception ex)
            {

            }
        }
    }


    //启动mqtt数据采集通道
    @Override
    public boolean startChannel()
    {
        if (has_started)
        {
            return true;
        }

        try
        {
            has_started = true;

            client = new MqttClient(SERVER_URL, clientid, new MemoryPersistence());
            options = new MqttConnectOptions();
            String[] ServerArray = new String[1];
            ServerArray[0] = SERVER_URL;
            options.setServerURIs(ServerArray);
            options.setCleanSession(true);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(80);    //60秒一次的心跳保持
            options.setAutomaticReconnect(true);   //设置自动重连

            //连接服务器
            client.setCallback(new MyChannelMqttCallback(this));
            client.connect(options);

            //启动发送和接收线程
            System.out.print("Start MQTT Work Thread!");

            is_working = true;
            thread_send = new ChannelSendThread(this);
            thread_rec= new ChannelRecThread(this);
            thread_send.start();
            thread_rec.start();

        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            err  ="";
        }

        return true;
    }

    @Override
    public void stopChannel()
    {
        if (null != client)
        {
            try
            {
                if (!client.isConnected())    //如果没有
                {
                    client.disconnect();
                }
                client.close();
            }
            catch (Exception ex)
            {

            }

        }

        has_started = false;
    }



    @Override
    public void SendThreadFun() {

        while (is_working)
        {
            if (queue_send.size() != 0)
            {
                MqttMsg msg = queue_send.poll();
                if (msg != null)
                {
                    try
                    {
                        client.publish(msg.topic,msg.preload,mqtt_info.getQs_level(),false);
                        Thread.sleep(10);
                    }
                    catch (Exception ex)
                    {

                    }

                }


            }
        }

    }

    //数据接收线程
    @Override
    public void RecThreadFun() {

        while (is_working)
        {

            //判断是否重连的，需要重新订阅
            if (is_need_ReSubcribe)
            {
                is_need_ReSubcribe = false;

                for(int i = 0; i < lstGather.size(); i++)
                {
                    IDeviceGather gather = lstGather.get(i);
                    UnsubcribeGather(gather);
                    SubcribeGather(gather);
                }
            }

            //判断是否有新挂接的设备数据采集器
            while(queue_newGather.size() > 0)
            {
                IDeviceGather newGather = queue_newGather.poll();
                if (null != newGather)
                {
                    RegisteNewGather(newGather);
                    UnsubcribeGather(newGather);
                    SubcribeGather(newGather);
                }
            }

            //判断是哪个设备的消息，直接调用它Gather
            while (queue_rec.size() != 0)
            {
                MqttMsg msg = queue_rec.poll();
                boolean bEnd = false;
                if (null != msg && msg.topic != null && !msg.equals(""))
                {

                    //循环查找每一个字符串
                    String sTopic = msg.topic;
                    List<String> lstItem = new ArrayList<String>();
                    while(sTopic != null && !bEnd)
                    {
                        int nPos = sTopic.indexOf('/');
                        if (nPos > 1)
                        {
                            String sItem = sTopic.substring(0,nPos);
                            lstItem.add(sItem);

                            //判断是否还有子串
                            if (sTopic.length() > nPos)
                            {
                                sTopic = sTopic.substring(nPos+1);
                            }
                            else
                            {
                                break;
                            }
                        }
                        else
                        {
                            if (nPos == 0)
                            {
                                break;
                            }
                            else
                            {
                                lstItem.add(sTopic);
                            }

                            bEnd = true;

                        }


                    }// while (queue_rec.size() != 0)

                    ProcessMsg(lstItem,msg.preload);
                }
            }


            try{
                Thread.sleep(100);
            }
            catch (Exception ex)
            {

            }
        }

    }

    //提取Product目录
    private void ProcessMsg(List<String> lstTopicItem,byte[] data)
    {
        if (null == lstTopicItem || lstTopicItem.size() < 3)
        {
            return ;
        }

        //判断是否处理产品信息
        String sProductPre = lstTopicItem.get(0);
        String sResponse = lstTopicItem.get(1);
        String sDeviceName =    lstTopicItem.get(2);
        if (sProductPre.equals("Gateway") && sResponse.equals("Response"))
        {
            IDeviceGather gather = getGather("MYGW-100",sDeviceName);
            if (null != gather)
            {
                DeviceMqttMsg msg = new DeviceMqttMsg(lstTopicItem,data);
                gather.onReceive(msg);
            }
        }
    }





    //通过产品名称和设备名称获取设备
    public IDeviceGather getGather(String sProduct,String sDeviceName)
    {
        IDeviceGather gather = null;

        for(int i = 0; i < lstGather.size(); i++)
        {
            gather = lstGather.get(i);
            if (null != gather)
            {
                IDevice device = gather.getGateWayDevice();
                if (null != device && device.getDeviceName().equals(sDeviceName) && device.getProductName().equals(sProduct))
                {
                    return gather;
                }
            }
        }

        return null;
    }

    //挂接新的设备数据采集器到通道中
    private void RegisteNewGather(IDeviceGather gather)
    {
        //添加设备通道时，添加设备的订阅消息
        if (null == gather )
        {
            return;
           // return PreConstant.ERR_CHANNEL_GATHER_NONE;
        }

        //获取采集器对应的网关设备
        IDevice device = gather.getGateWayDevice();
        if (null == device)
        {
            return;
          //  return PreConstant.ERR_CHANNEL_DEVICE_NONE;
        }

        //检查添加的设备是否重复
        if (getGather(device.getProductName(),device.getDeviceName()) != null)
        {
            return;
          //  return PreConstant.ERR_CHANNEL_GATHER_REPEAT;
        }

        //添加到队列中
        lstGather.add(gather);

        //添加设备的订阅
        SubcribeGather(gather);


        //添加设备属性的订阅
      /*  List<IProductProperty> lstProperty = device.getPropertys();
        for(int i = 0; i < lstProperty.size(); i++)
        {
            IProductProperty property = lstProperty.get(i);
            if (null != property)
            {
                String sTopic = "/Product/"
                        +property.getProductName()+"/"
                        +property.getDevName()+"/"
                        +"PropertyFresh/"
                        +property.getPropertyName();

                try
                {
                    client.subscribe(sTopic);
                }
                catch (Exception ex)
                {

                }
            }
        }*/
    }

    //添加数据采集器，同时添加订阅
    @Override
    public int addGather(IDeviceGather gather) {

        //将新加入的设备数据采集器放在消息队列缓冲，由数据接收处理线程负责注册通道，保证设备采集器队列的集合操作的
        //同步性
        try
        {
            queue_newGather.put(gather);
        }
        catch (Exception ex)
        {

        }

        return 0;
    }

    @Override
    public int detachGather(IDeviceGather gather) {
        return 0;
    }

    @Override
    public boolean addFilter(IChannelFilter filter) {
        return false;
    }

    @Override
    public void detachFilter(IChannelFilter filter) {

    }


}
