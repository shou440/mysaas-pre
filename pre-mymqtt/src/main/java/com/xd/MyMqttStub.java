package com.xd;

import io.undertow.client.PushCallback;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;


/**
 * <p>
 * MQTT通讯功能模块
 * </p>
 *
 * @author zxr
 * @since 2020-02-04
 */
public class MyMqttStub {


    private static MyMqttStub sinTon = null;
    public static final String SERVER_URL = "tcp://211.149.169.214:5009";
    public static final String clientid  = "client4";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "mbclient";
    private String passWord = "my3332361";
    private ScheduledExecutorService scheduler;
    private List<String> lstTopic = new ArrayList<String>();

    private MyPushCallback mqttCK = null;
    private boolean is_working = false;

    //任务线程
    private Thread thread_send= null;
    private Thread thread_rec = null;

    //已经启动的标志位
    private boolean has_started  = false;

    //数据发送队列缓冲
    private static LinkedBlockingQueue<MqttMsg> queue_send = new LinkedBlockingQueue<MqttMsg>();

    //数据接收队列缓冲
    private static LinkedBlockingQueue<MqttMsg> queue_rec = new LinkedBlockingQueue<MqttMsg>();

    /**
     * 获取单件对象
     *
     */
    public static MyMqttStub getTheMyMqttStub()
    {
        if (null == sinTon)
        {
            sinTon = new MyMqttStub();
        }

        return  sinTon;
    }

    //判断是否已经启动
    public boolean hasStarted()
    {
        return  has_started;
    }

    /*
        关闭连接
     */
    public void Close()
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
    }

    /*
        设置
     */
    public void SetSubcribe(String topic)
    {
        lstTopic.add(topic);
    }

    /*
        订阅消息
     */
    private void Subcribe()
    {
        try
        {
            int[] Qos = {1};
            int nLen = lstTopic.size();
            for(int i = 0; i < nLen; i++)
            {
                String sTopic = lstTopic.get(i);
                client.subscribe(sTopic,1);
            }

        }
        catch (Exception ex)
        {
            return ;
        }
    }


    /**
     * 启动MQTT服务功能
     *
     */
    public void StartService() {

        if (has_started)
        {
            return;
        }

        try
        {
            has_started = true;

            client = new MqttClient(SERVER_URL, clientid, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(200);
            options.setKeepAliveInterval(2000);

            //连接服务器
            client.setCallback(new MyPushCallback());
            client.connect(options);

            //设置订阅的消息
            Subcribe();

            //启动线程
            System.out.print("Start MQTT Work Thread!");
            thread_send = new Thread(new MqttSendThread());
            thread_send.start();

            thread_rec= new Thread(new MqttRecThread());
            thread_rec.start();

        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            err  ="";
        }

    }
    /**
     * 数据发布
     *
     */
    public void publish(int qos,boolean retained,String topic,String sData){



        MqttMsg msg = new MqttMsg(topic, sData.getBytes());

        try{
            queue_send.put(msg);
        }
        catch (Exception ex)
        {

        }

    }

    public void publish(int qos,boolean retained,String topic,byte[] data){

        MqttMsg msg = new MqttMsg(topic, data);

        try{
            queue_send.put(msg);
        }
        catch (Exception ex)
        {

        }
    }

    //提取消息
    public MqttMsg FetchRecMsg()
    {
        if (queue_rec.size() == 0)
        {
            return null;
        }

        return queue_rec.poll();
    }

    //接收信息
    public void OnReceive(String topic, MqttMessage message)
    {
        MqttMsg msg = new MqttMsg();
        msg.topic = topic;
        msg.preload = message.getPayload();

        //通过Topic获取是哪个终端单元
        try
        {
            queue_rec.put(msg);
        }
        catch (Exception ex)
        {

        }

    }

    //工作线程
    public void SendThread() {

        is_working = true;
        while(is_working)
        {
            int counter = 0;
            while(queue_send.size() != 0 && ++counter < 50)
            {
                MqttMsg msg = queue_send.poll();
                if (null != msg)
                {
                    try{
                        client.publish(msg.topic,msg.preload,1,false);

                    }
                    catch (Exception ex)
                    {
                        System.out.print("Mqtt Send Exception:"+ex.getMessage());
                    }
                }
            }


            try {
                Thread.sleep(20);
            }
            catch (Exception ex)
            {

            }

        }
    }

    //数据接收线程
    public void RecThread() {

        is_working = true;
        while(is_working)
        {



            try {
                Thread.sleep(10);
            }
            catch (Exception ex)
            {

            }

        }
    }
}
