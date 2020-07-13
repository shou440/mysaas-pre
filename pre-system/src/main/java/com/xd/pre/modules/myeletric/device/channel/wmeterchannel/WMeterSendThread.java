package com.xd.pre.modules.myeletric.device.channel.wmeterchannel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class WMeterSendThread extends Thread {

    private WMeterUDPChannel channel = null;
    DatagramPacket datagramPacket = null;
    private byte[] devCode = null;


    public WMeterSendThread(DatagramPacket packet,WMeterUDPChannel chl,byte[] code) {

        this.datagramPacket = packet;
        this.channel =  chl;
        devCode = code;
    }

    @Override
    public void run() {


        super.run();

        if(null != channel)
        {
            channel.ProcessNewCall(datagramPacket,devCode);
        }
    }


}
