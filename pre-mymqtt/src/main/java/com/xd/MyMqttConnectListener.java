package com.xd;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;
import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;

public class MyMqttConnectListener extends ConnectActionListener {

    public MyMqttConnectListener(MqttAsyncClient client, MqttClientPersistence persistence, ClientComms comms, MqttConnectOptions options, MqttToken userToken, Object userContext, IMqttActionListener userCallback, boolean reconnect) {
        super(client, persistence, comms, options, userToken, userContext, userCallback, reconnect);
    }
}
