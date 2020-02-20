package com.xd;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import java.io.IOException;


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
    private IMqttAsyncClient client = null;
    private MyPushCallback mqttCK = null;

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

    /**
     * 启动MQTT服务功能
     *
     */
    public void StartService() {

        try
        {
            DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

            //连接选项
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setUserName("mbclient");
            String sPwd= "my3332361";
            opts.setPassword(sPwd.toCharArray());
            opts.setAutomaticReconnect(true);
            opts.setCleanSession(true);

            client = factory.getAsyncClientInstance("tcp://211.149.169.214:5009","test111");

            //设置回调函数
            mqttCK = new MyPushCallback();
            client.setCallback(mqttCK);

            //连接服务器
            client.connect(opts);

        }
        catch (Exception ex)
        {

        }

    }
    /**
     * 数据发布
     *
     */
    public void publish(int qos,boolean retained,String topic,String sData){

        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(sData.getBytes());

        try
        {
            client.publish(topic,message);
        }
        catch (Exception ex)
        {
            String sErr = ex.getMessage();
        }
    }

}
