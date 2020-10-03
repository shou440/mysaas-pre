package com.xd.pre.modules.myeletric.device.gather;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.command.*;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.IProductSignal;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import org.aspectj.bridge.ICommand;

//MY610-E的数据解析器
public class My610ESubDevice implements IMyMqttSubDevice {

    private  IDevice sub_device = null;
    public   static final String  PRODUCT_NAME = "MY610-ENB";
    private  int     fresh_tick = 0;
    private  IDeviceGather device_gather =null;
    private  boolean clock_flag = true;

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
    public IDeviceGather gather() {
        return device_gather;
    }

    @Override
    public boolean IsNeedClock() {
        return false;
    }


    @Override
    public boolean IsNeedResend() {
        return clock_flag;
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



    //发送插入命令
    @Override
    public void SendCommand(IMyCommand command) {

        if (null == command)
        {
            return;
        }

        switch (command.getCommandType())
        {
            case IMyCommand.COMMAND_CHARGE:                    //充值命令
            {
                System.out.print("发送充值命令");
                SendChargeCmd(command);
                break;
            }
            case IMyCommand.COMMAND_MANNER_ADJUST:             //退电调整
            {
                System.out.print("发送退电命令");
                AdjustDDCommand(command);
                break;
            }
            case IMyCommand.COMMAND_MANNER_CLEARLEFT:         //清零剩余电度
            {
                System.out.print("发送清零剩余电度命令");
                ClearLeftDDCommand(command);
                break;
            }
        }



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
        int nNO =sub_device.getDevNO();
        int index = 0;

        cmd[index++] = (byte)((nNO)&0xFF);
        cmd[index++] = 0x03;

        //在很蛮有写明金陵要求的
        cmd[index++] = 0x01;   //寄存器起始地址
        cmd[index++] = 0x00;   //寄存器起始地址

        //请求的Tick
        cmd[index++] = 0x00;   //读取个数
        cmd[index++] = 33;


        SendAsdu(cmd);

    }

    /*****************************************************************************************************
     *                                  SendChargeCmd
     *  描      述 :  电度充值命令
     * 输 入 参 数: 1 command,充值命令
     * 返  回   值: 无
     *
     *****************************************************************************************************/
    private void SendChargeCmd(IMyCommand command)
    {
        MyChargeCommand chargeCmd =  (MyChargeCommand)command;
        if (null == chargeCmd)
        {
            return;
        }

        //提取充值的金额
        //采用子索引作为设备的Modbus地址
        byte[] cmd = new byte[14];
        int nNO =sub_device.getDevNO();
        int index = 0;
        float fDD = chargeCmd.getChargeEP()*10;
        int nDD = (int)fDD;

        cmd[index++] = (byte)((nNO)&0xFF);

        //充值命令,执行命令序列号
        cmd[index++] = (byte)0x40;

        //命令序列号
        int nCommandSN = chargeCmd.getCommandSN();
        cmd[index++] = (byte)((nCommandSN >> 24)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 16)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 8)&0xFF);
        cmd[index++] = (byte)((nCommandSN )&0xFF);

        //超时Tick
        int nExipreTick = chargeCmd.ExipredTick();
        cmd[index++] = (byte)((nExipreTick >> 24)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 16)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 8)&0xFF);
        cmd[index++] = (byte)((nExipreTick )&0xFF);

        //充值的电度数
        cmd[index++] = (byte)((nDD >> 24)&0xFF);
        cmd[index++] = (byte)((nDD >> 16)&0xFF);
        cmd[index++] = (byte)((nDD >> 8)&0xFF);
        cmd[index++] = (byte)((nDD )&0xFF);

        if( null != device_gather)
        {
            System.out.print("发送充值4");
        }

        SendAsdu(cmd);
    }
    /*****************************************************************************************************
     *                                  ClearLeftDDCommand
     *  描      述 :  发送清零剩余电度命令
     * 输 入 参 数: 1 command,充值命令
     * 返  回   值: 无
     *
     *****************************************************************************************************/
    @Override
    public void ClearLeftDDCommand(IMyCommand command) {

        MyChargeCommand chargeCmd =  (MyChargeCommand)command;
        if (null == chargeCmd)
        {
            return;
        }

        //提取充值的金额
        //采用子索引作为设备的Modbus地址
        byte[] cmd = new byte[14];
        int nNO =sub_device.getDevNO();
        int index = 0;
        float fDD = chargeCmd.getChargeEP()*1000;
        int nDD = (int)fDD;

        cmd[index++] = (byte)((nNO)&0xFF);

        //清零剩余电度命令,执行命令序列号
        cmd[index++] = (byte)0x41;

        //命令序列号
        int nCommandSN = chargeCmd.getCommandSN();
        cmd[index++] = (byte)((nCommandSN >> 24)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 16)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 8)&0xFF);
        cmd[index++] = (byte)((nCommandSN )&0xFF);

        //超时Tick
        int nExipreTick = chargeCmd.ExipredTick();
        cmd[index++] = (byte)((nExipreTick >> 24)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 16)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 8)&0xFF);
        cmd[index++] = (byte)((nExipreTick )&0xFF);

        //充值的电度数
        cmd[index++] = (byte)((nDD >> 24)&0xFF);
        cmd[index++] = (byte)((nDD >> 16)&0xFF);
        cmd[index++] = (byte)((nDD >> 8)&0xFF);
        cmd[index++] = (byte)((nDD )&0xFF);

        if( null != device_gather)
        {
            System.out.print("发送清零剩余电度\n");
        }

        SendAsdu(cmd);
    }

    @Override
    public void ClearTotalDDCommand(IMyCommand command) {

    }

    @Override
    public void AdjustDDCommand(IMyCommand command) {

        MyMeterDrawbackCommand drawbackCommand =  (MyMeterDrawbackCommand)command;
        if (null == drawbackCommand)
        {
            return;
        }

        //提取充值的金额
        //采用子索引作为设备的Modbus地址
        byte[] cmd = new byte[14];
        int nNO =sub_device.getDevNO();
        int index = 0;
        float fDD = drawbackCommand.getDrawbackEP()*10;
        int nDD = (int)fDD;

        cmd[index++] = (byte)((nNO)&0xFF);

        //退电命令,执行命令序列号
        cmd[index++] = (byte)0x43;

        String str = String.format("退电命令序号%06d\n",drawbackCommand.getCommandSN());
        System.out.print(str);

        //命令序列号
        int nCommandSN = drawbackCommand.getCommandSN();
        cmd[index++] = (byte)((nCommandSN >> 24)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 16)&0xFF);
        cmd[index++] = (byte)((nCommandSN >> 8)&0xFF);
        cmd[index++] = (byte)((nCommandSN )&0xFF);

        //超时Tick
        int nExipreTick = drawbackCommand.ExipredTick();
        cmd[index++] = (byte)((nExipreTick >> 24)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 16)&0xFF);
        cmd[index++] = (byte)((nExipreTick >> 8)&0xFF);
        cmd[index++] = (byte)((nExipreTick )&0xFF);

        //充值的电度数
        cmd[index++] = (byte)((nDD >> 24)&0xFF);
        cmd[index++] = (byte)((nDD >> 16)&0xFF);
        cmd[index++] = (byte)((nDD >> 8)&0xFF);
        cmd[index++] = (byte)((nDD )&0xFF);

        if( null != device_gather)
        {
            System.out.print("发送退电\n");
        }

        SendAsdu(cmd);

    }

    /*****************************************************************************************************
     *                                  ProcessData
     *  描      述 :  处理接收到的数据
     * 输 入 参 数: 1 data,接收到的数据
     * 返  回   值: true:成功,false:失败
     *
     *****************************************************************************************************/
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
           return  false;
        }

        if(data[1] != 0x03 || data[2] != 66 || data.length < 70)
        {
            return  false;
        }

        if (null == sub_device)
        {
            return false;
        }

        //提取电表的状态信号
        index = 3;
        int nAlramWord = (data[index++]&0xFF);
        nAlramWord <<= 8;
        nAlramWord += (data[index++]&0xFF);

        //提取拉合闸信号
        IProductSignal  signal = sub_device.getSignal("switch_signal");
        if(null != signal)
        {
            if ((nAlramWord&0x01) == 1)
            {
                signal.setValue(1);
            }
            else
            {
                signal.setValue(0);
            }
        }

        //提取余额告警
        signal = sub_device.getSignal("balance_alaram");
        if(null != signal)
        {
            if (((nAlramWord>>7)&0x01) == 1)
            {
                signal.setValue(1);
            }
            else
            {
                signal.setValue(0);
            }
        }

        //电度取异常
        signal = sub_device.getSignal("dd_rom_abort");
        if(null != signal)
        {
            if (((nAlramWord>>2)&0x01) == 1)
            {
                signal.setValue(1);
            }
            else
            {
                signal.setValue(0);
            }
        }

        //参数区异常
        signal = sub_device.getSignal("param_rom_abort");
        if(null != signal)
        {
            if (((nAlramWord>>3)&0x01) == 1)
            {
                signal.setValue(1);
            }
            else
            {
                signal.setValue(0);
            }
        }

        //记录区异常
        signal = sub_device.getSignal("record_rom_abort");
        if(null != signal)
        {
            if (((nAlramWord>>4)&0x01) == 1)
            {
                signal.setValue(1);
            }
            else
            {
                signal.setValue(0);
            }
        }

        //校时标志位
        signal = sub_device.getSignal("clock_flag");
        if(null != signal)
        {
            if (((nAlramWord>>5)&0x01) == 1)
            {
                signal.setValue(1);
                clock_flag = true;
            }
            else
            {
                signal.setValue(0);
                clock_flag = false;
            }
        }

        //提取电表的时钟数据
        int nClock = (data[index++]&0xFF);
        nClock <<= 8;
        nClock += (data[index++]&0xFF);
        nClock <<= 8;
        nClock += (data[index++]&0xFF);
        nClock <<= 8;
        nClock += (data[index++]&0xFF);


        String devName = sub_device.getDeviceName();

        //电流
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        IProductProperty property = sub_device.getProperty("I");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=100000;
            String sKey = "devpro-"+devName+"-I";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);

        }

        //电压
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("U");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-U";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //有功
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("P");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-P";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //无功
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("Q");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-Q";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //视在功率
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("S");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-S";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //功率因数
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("cos");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-cos";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //跳过2个测量量
        index +=8;

        //信号强度
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("signal");
        if (null != property)
        {
            property.setValue(nData);

            //保存到Redis中
            float fValue = nData;
            fValue /=1000;
            String sKey = "devpro-"+devName+"-signal";
            String sValue = String.format("%f",fValue);
            MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
        }

        //累计电度
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData +=(data[index++]&0xFF);
        nData <<= 8;
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
                devName = sub_device.getDeviceName();

                ProductionContainer.getTheMeterDeviceContainer().FreshTotalEpFroRedis(devName,property.getFloatValue()/18);

            }
        }

        index += 8;

        //剩余电度
        nData = (data[index++]&0xFF);
        nData <<= 8;
        nData +=(data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        nData <<= 8;
        nData += (data[index++]&0xFF);
        property = sub_device.getProperty("left_ep");

        System.out.print(String.format("剩余电度%d",nData));
        if (null != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);

            //刷新剩余电度
            if (sub_device != null)
            {
                ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
            }

        }

        //更新抄表时间
        if (sub_device != null)
        {
            devName = sub_device.getDeviceName();
            int nTick = CommonFun.GetTick();
            ProductionContainer.getTheMeterDeviceContainer().FreshTickFroRedis(devName,nTick);
        }

        return  true;
    }


    //处理充值命令
    private boolean ProcessChargeCommand(byte[] rec,MyChargeCommand chargeCommand)
    {
        int code = (rec[1])&0xFF;
        String str = String.format("接收到电表充电数据长度%d\n",rec.length);
        System.out.print(str);
        if (null == rec || chargeCommand == null || rec.length != 16 || code != 0x40)
        {
            return false;
        }

        //提取充值以后的剩余电量
        int nCommandSN = 0;
        int index = 2;
        nCommandSN = (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN +=(rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        if (nCommandSN != chargeCommand.getCommandSN())
        {
            return false;
        }

        int nData = 0;

        //累计电度
        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        IProductProperty property = sub_device.getProperty("total_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }


        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        property = sub_device.getProperty("left_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }

        if (null != chargeCommand)
        {
            MyCommandMessage msg  = new MyCommandMessage(MyCommandMessage.CMD_MSG_CMPLT,"");
            chargeCommand.onMessage(msg);
        }



        return true;
    }

    //处理退电命令
    private boolean ProcessDrawbackCommand(byte[] rec,MyMeterDrawbackCommand drawbackCommand)
    {
        int code = (rec[1])&0xFF;
        String str = String.format("接收到电表退电数据长度%d\n",rec.length);
        System.out.print(str);
        if (null == rec || drawbackCommand == null || rec.length != 16 || code != 0x43)
        {
            return false;
        }

        //提取充值以后的剩余电量
        int nCommandSN = 0;
        int index = 2;
        nCommandSN = (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN +=(rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        if (nCommandSN != drawbackCommand.getCommandSN())
        {
            return false;
        }

        int nData = 0;
        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        IProductProperty property = sub_device.getProperty("total_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }


        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        property = sub_device.getProperty("left_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }

        if (null != drawbackCommand)
        {
            MyCommandMessage msg  = new MyCommandMessage(MyCommandMessage.CMD_MSG_CMPLT,"");
            drawbackCommand.onMessage(msg);
        }



        return true;
    }

    //处理清零剩余电度命令
    private boolean ProcessClearLeftDDCommand(byte[] rec,MyClearLeftEPCommand clearLeftCommand)
    {
        int code = (rec[1])&0xFF;
        String str = String.format("接收到电表清零电度数据长度%d\n",rec.length);
        System.out.print(str);
        if (null == rec || clearLeftCommand == null || rec.length != 16 || code != 0x41)
        {
            return false;
        }

        //提取充值以后的剩余电量
        int nCommandSN = 0;
        int index = 2;
        nCommandSN = (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN +=(rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        nCommandSN <<= 8;
        nCommandSN += (rec[index++]&0xFF);
        if (nCommandSN != clearLeftCommand.getCommandSN())
        {
            return false;
        }

        int nData = 0;
        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        IProductProperty property = sub_device.getProperty("total_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }


        nData = (rec[index++]&0xFF);
        nData <<= 8;
        nData +=(rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        nData <<= 8;
        nData += (rec[index++]&0xFF);
        property = sub_device.getProperty("left_ep");
        if (null  != property)
        {
            float fValue = nData;
            fValue /=1800;
            property.setFloatValue(fValue);
            property.setFloatValue(fValue);

            String devName = sub_device.getDeviceName();
            ProductionContainer.getTheMeterDeviceContainer().FreshLeftEpFroRedis(devName,property.getFloatValue());
        }

        if (null != clearLeftCommand)
        {
            MyCommandMessage msg  = new MyCommandMessage(MyCommandMessage.CMD_MSG_CMPLT,"");
            clearLeftCommand.onMessage(msg);
        }



        return true;
    }

    //将错误代码,将错误代码翻译成错误描述，然后通知执行命令
    protected boolean CheckErrorResponse(byte[] data,IMyCommand command)
    {
        int index = 2;

        if (data.length < 5 || (data[1]&0xFF) != 0x82)
        {
            return false;
        }

        System.out.print("命令执行失败1\n");

        //提取错误代码
        int nErrCode = (data[index++]&0xFF);
        nErrCode <<= 8;
        nErrCode +=(data[index++]&0xFF);


        String errMsg = "执行错误";
        switch (nErrCode)
        {
            case 0x01:
            {
                errMsg = "命令流水号已失效";
                break;
            }
            case 0x02:
            {
                errMsg = "命令已过期";
                break;
            }
            case 0x03:
            {
                errMsg = "退电额度超过剩余电度";
                break;
            }
        }

        if (command != null)
        {
            MyCommandMessage msg  = new MyCommandMessage(MyCommandMessage.CMD_MSG_ABORT,errMsg);
            command.onMessage(msg);

            System.out.print("命令执行失败:"+errMsg+"\n");
        }

        return true;

    }

    //处理接收到的命令
    @Override
    public boolean ProcessCommand(byte[] data,IMyCommand command) {
        int index = 0;
        int nData = 0;



        if(null == data || data.length < 5 || command == null)
        {
            return false;
        }

        if (!checkReceive(data) )
        {
             return  false;
        }

        if (CheckErrorResponse(data,command))
        {
            return  false;
        }


        switch (command.getCommandType())
        {
            case IMyCommand.COMMAND_CHARGE:
            {
                if (ProcessChargeCommand(data,(MyChargeCommand)command))
                {
                    return true;
                }
                break;
            }
            case IMyCommand.COMMAND_MANNER_ADJUST:
            {
                if (ProcessDrawbackCommand(data,(MyMeterDrawbackCommand) command))
                {
                    return true;
                }
                break;
            }
            case IMyCommand.COMMAND_MANNER_CLEARLEFT:     //清零剩余电度
            {
                if (ProcessClearLeftDDCommand(data,(MyClearLeftEPCommand) command))
                {
                    return true;
                }
                break;
            }
        }



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
        int nAddr = (data[0]&0xFF);
        if (nAddr != (sub_device.getDevNO()&0xFF))
        {
            return false;
        }

        //检查校验码是否正确
        int sum = getCRC(data,data.length-2);
        int checksum = (int)(data[data.length-1]&0xFF);
        checksum <<= 8;
        checksum += (data[data.length-2]&0xFF);
        if (sum != checksum)
        {
            return  false;
        }

        //检测接收数据成功则注册在线
        if (null != sub_device){


            System.out.print("获取设备号"+sub_device.getDeviceName()+"注册上线\n");
            sub_device.OnLineRegiste();
        }

        return true;
    }
}
