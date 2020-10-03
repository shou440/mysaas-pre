package com.xd.pre.modules.myeletric.device.gather;

import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.production.IDevice;

public class My630ESubDevice implements IMyMqttSubDevice {

    private IDevice sub_device = null;
    public static final String  PRODUCT_NAME = "MY630-ENB";
    private int     fresh_tick = 0;
    private  IDeviceGather device_gather =null;
    private boolean isneed_clock = true;

    public  My630ESubDevice(IDevice device,IDeviceGather gather)
    {
        sub_device  = device;
        fresh_tick = 0;
        device_gather = gather;
    }


    @Override
    public IDevice getDevice() {
        return null;
    }

    @Override
    public IDeviceGather gather() {
        return  device_gather;
    }

    @Override
    public boolean IsNeedClock() {
        return isneed_clock;
    }


    @Override
    public boolean IsNeedResend() {
        return false;
    }

    @Override
    public int LastFreshTick() {
        return 0;
    }

    @Override
    public void SendCallQuestCmd() {

    }

    @Override
    public void SendCommand(IMyCommand command) {

    }

    @Override
    public void ClearLeftDDCommand(IMyCommand command) {

    }

    @Override
    public void ClearTotalDDCommand(IMyCommand command) {

    }

    @Override
    public void AdjustDDCommand(IMyCommand command) {

    }

    @Override
    public boolean ProcessData(byte[] data) {
        return false;
    }

    @Override
    public boolean ProcessCommand(byte[] data, IMyCommand command) {
        return false;
    }

    @Override
    public boolean checkReceive(byte[] data) {
        return false;
    }
}
