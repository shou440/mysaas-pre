package com.xd.pre.modules.myeletric.device.channel;

import com.xd.MqttMsg;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyChannelMqttCallback implements MqttCallbackExtended {

    private MyMqttChannel channel = null;

    public MyChannelMqttCallback(MyMqttChannel channelTmp)
    {
        channel = channelTmp;
    }

    @Override
    public void connectionLost(Throwable cause) {

        if (null != channel)
        {
            channel.OnDisconnected();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        if (null != channel)
        {
            MqttMsg msg = new MqttMsg(topic,message.getPayload());
            channel.OnReceive(msg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

        if (reconnect && null != channel)
        {
            channel.OnReconnected();
            System.out.print("Mqtt Reconnected"+serverURI);
        }
    }
}
