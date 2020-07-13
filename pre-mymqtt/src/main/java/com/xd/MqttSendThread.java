package com.xd;

public class MqttSendThread implements Runnable {

    private boolean is_working = false;

    @Override
    public void run() {

        MyMqttStub.getTheMyMqttStub().SendThread();
    }
}
