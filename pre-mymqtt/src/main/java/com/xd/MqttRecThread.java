package com.xd;

public class MqttRecThread implements Runnable {


    @Override
    public void run() {

        MyMqttStub.getTheMyMqttStub().RecThread();
    }
}
