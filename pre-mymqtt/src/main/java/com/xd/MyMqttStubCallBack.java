package com.xd;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttStubCallBack implements MqttCallbackExtended {



    @Override
    public void connectionLost(Throwable cause) {

       MyMqttStub.getTheMyMqttStub().Close();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {


            MqttMsg msg = new MqttMsg(topic,message.getPayload());
            MyMqttStub.getTheMyMqttStub().OnReceive(topic,message);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

        if (reconnect )
        {
            MyMqttStub.getTheMyMqttStub().OnReconnected();
            System.out.print("Mqtt Reconnected"+serverURI);
        }
    }
}
