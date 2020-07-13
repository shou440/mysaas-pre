package com.xd.pre.modules.myeletric.device.gather;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import org.aspectj.bridge.ICommand;

//MY610-E的数据解析器
public class My610ESubDevice implements IMyMqttSubDevice {

    private IDevice sub_device = null;
    public static final String  PRODUCT_NAME = "MY610-ENB";
    private int     fresh_tick = 0;
    private  IDeviceGather device_gather =null;


    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static int getCRC(byte[] bytes,int nLen) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        if (bytes.length < nLen)
        {
            return 0;
        }

        int i, j;
        for (i = 0; i < nLen; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }



    public  My610ESubDevice(IDevice device,IDeviceGather gather)
    {
        sub_device  = device;
        fresh_tick = 0;
        device_gather = gather;
    }

    @Override
    public IDevice getDevice() {
        return sub_device;
    }

    @Override
    public boolean IsNeedResend() {
        return false;
    }

    @Override
    public int LastFreshTick() {
        return 0;
    }


    public void SendAsdu(byte[] data)
    {
        int len = 0;
        if (null == data)
        {
            return;
        }
        len = data.length;
        byte[] asdu = new byte[len+2];

        int sum = getCRC(data,data.length);
        for(int i = 0; i < len; i++)
        {
            asdu[i] = data[i];
        }
        asdu[len] = (byte)(sum&0xFF);
        asdu[len+1] = (byte)((sum>>8)&0xFF);


        device_gather.SendData(asdu);
    }

    @Override
    public void SendCallQuestCmd() {

        if (sub_device == null)
        {
            return;
        }
        if(null == device_gather)
        {
            return;
        }

        //采用子索引作为设备的Modbus地址
        byte[] cmd = new byte[6];
        int nNO =sub_device.getSubIndex();
        int index = 0;

        cmd[index++] = (byte)((nNO)&0xFF);
        cmd[index++] = 0x03;

        //在很蛮有写明金陵要求的
        cmd[index++] = 0x05;   //寄存器起始地址
        cmd[index++] = 0x00;   //寄存器起始地址

        //请求的Tick
        cmd[index++] = 0x00;   //读取个数
        cmd[index++] = 11;


        SendAsdu(cmd);

    }

    @Override
    public void SendCommand(ICommand command) {

    }

    @Override
    public boolean ProcessData(byte[] data) {

        int index = 0;
        int nData = 0;
        if(null == data)
        {
            return false;
        }

        if (!checkReceive(data))
        {
           // return  false;
        }

        if(data[1] != 0x03 || data[2] != 22 || data.length != 27)
        {
            return  false;
        }

        if (null == sub_device)
        {
            return false;
        }

        //提取遥信数据和报警数据
        index = 3;
        int nAlarm = (data[index++]&0xFF);
        int nYX = (data[index++]&0xFF);

        //电流
        nData = data[index++]&0xFF;
        nData <<= 8;
        nData += (data[index++]&0xFF);
        IProductProperty property = sub_device.getProperty("Ia");
        if (null != property)
        {
            property.setValue(nData);
        }

        //电压
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("Ua");
        if (null != property)
        {
            property.setValue(nData);
        }

        //功率
        nData = (data[index++]&0xFF);
        nData <<= 24;
        nData += (data[index++]&0xFF);
        nData <<= 16;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("Pa");
        if (null != property)
        {
            property.setValue(nData);
        }

        //功率因数
        nData = (data[index++]&0xFF);
        nData <<= 24;
        nData += (data[index++]&0xFF);
        nData <<= 16;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("Cos");
        if (null != property)
        {
            property.setValue(nData);
        }

        //累计电度
        nData = (data[index++]&0xFF);
        nData <<= 24;
        nData +=(data[index++]&0xFF);
        nData <<= 16;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("total_ep");
        if (null != property)
        {
            property.setValue(nData);


            //将电度数据和剩余电度记录到Redis中
            if (sub_device != null)
            {
                String devName = sub_device.getDeviceName();

                ProductionContainer.getTheMeterDeviceContainer().FreshTotalEpFroRedis(devName,property.getFloatValue()/18);

            }
        }


        //剩余电度
        nData = data[index++];
        nData <<= 24;
        nData += data[index++];
        nData <<= 16;
        nData += data[index++];
        nData <<= 8;
        nData += data[index++];
        property = sub_device.getProperty("left_ep");
        if (null != property)
        {
            property.setValue(nData);

            //刷新剩余电度
            if (sub_device != null)
            {
                String devName = sub_device.getDeviceName();
                ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());

            }

        }

        //更新抄表时间
        if (sub_device != null)
        {
            String devName = sub_device.getDeviceName();
            int nTick = CommonFun.GetTick();
            ProductionContainer.getTheMeterDeviceContainer().FreshTickFroRedis(devName,nTick);
        }

        return  true;
    }

    @Override
    public boolean ProcessCommand(byte[] data) {
        return false;
    }


    //检查接收的数据是否正确
    @Override
    public boolean checkReceive(byte[] data) {

        if (null == data || sub_device == null || data .length < 3)
        {
            return  false;
        }

        //检查地址是否正确
        int nAddr = data[0];
        if (nAddr != sub_device.getSubIndex())
        {
            return false;
        }

        //检查校验码是否正确
        int sum = getCRC(data,data.length-2);
        if ((sum&0xFF) != data[data.length-2]
            || ((sum>>8)&0xFF)!= data[data.length-1])
        {
            return  false;
        }

        return true;
    }
}
