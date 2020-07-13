package com.xd.pre.modules.myeletric.device.channel.wmeterchannel;


import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.channel.filter.IChannelFilter;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyChannelInfo;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;


//水表UDP通道
public class WMeterUDPChannel implements IMyChannel {

    //通道号
    private int channel_id = 0;

    //通道名称
    private String channelName = "";

    //udp服务器网络套接字
    private DatagramSocket server_udp_socket = null;


    // 创建字节数组，指定接收的数据包的大小
    byte[] rec_buffer = new byte[1024];

    //服务器
    private MyChannelInfo mqtt_info = null;

    private String SERVER_URL = "tcp://134.175.52.44:6100";
    private ScheduledExecutorService scheduler;

    private boolean is_working = false;

    //任务线程
    private WMeterChannelListenThread thread_listen= null;
    //private ChannelRecThread thread_listen = null;

    //已经启动的标志位
    private boolean has_started  = false;




    //新挂接到通道下的设备数据采集器
    private  LinkedBlockingQueue<IDeviceGather> queue_newGather = new LinkedBlockingQueue<IDeviceGather>();

    //带信息的构造函数
    public WMeterUDPChannel(MyChannelInfo info)
    {
        mqtt_info = info;
        if (null != info)
        {
            channelName = info.getChannel_name();
            channel_id = info.getChannel_id();
        }
    }

    @Override
    public int getChannelID() {
        return 0;
    }

    @Override
    public void setName(String Name) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void sendData(Object data) {

    }

    @Override
    public void OnReceive(Object data) {

    }

    //判断数据帧类型，做不同的处理
    public int getFrameType(byte[] rec)
    {
        int index = 2;

        if (null == rec || rec.length < 45 )
        {
            return -1;
        }

        byte ctrlCode = rec[14];
        ctrlCode &= 0x0F;
        int code = ctrlCode;
        return  code;
    }


    //提取设备号
    private byte[] getDevCode_byt(byte[] rec)
    {
        if (null == rec || rec.length < 45)
        {
            return null;
        }

        byte[] Cmd = new byte[8];
        int index = 2;
        Cmd[0] = rec[index++];
        Cmd[1] = rec[index++];
        Cmd[2] = rec[index++];
        Cmd[3] = rec[index++];
        Cmd[4] = rec[index++];
        Cmd[5] = rec[index++];
        Cmd[6] = rec[index++];
        Cmd[7] = rec[index++];

        return Cmd;
    }

    //发送点抄请求
    private byte[] getSendCmd(byte[] devCode)
    {

        int index = 0;

        byte[] cmd = new byte[45];
        cmd[index++] = 0x68;

        //水表类型
        cmd[index++] = 00;

        //8位设备编号的BCD码
        for(int i = 0; i < 8; i++)
        {
            cmd[index++] = devCode[i];
        }

        //4位预留的Y1
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;

        //控制码,点抄命令
        cmd[index++] = (byte)0x85;

        //加密方式，明文
        cmd[index++] = (byte)0x01;

        //加密方法，待定
        cmd[index++] = (byte)0x00;

        //随机数8位,填0
        for(int i = 0; i <8; i++)
        {
            cmd[index++] = (byte)0x00;
        }

        //SN8位,填0
        for(int i = 0; i <8; i++)
        {
            cmd[index++] = (byte)0x00;
        }

        //采集器个数,NB单个0x00
        cmd[index++] = (byte)0x00;

        //4个字节的预留
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;
        cmd[index++] = 0x00;

        //数据长度2字节,长度默认3
        cmd[index++] = (byte)0x00;
        cmd[index++] = (byte)0x03;

        //数据域
        cmd[index++] = (byte)0x01;
        cmd[index++] = (byte)0x01;
        cmd[index++] = (byte)0x00;

        //校验码
        int sum = 0;
        for(int i = 0; i < index; i++)
        {
            sum += cmd[i];
        }

        cmd[index++] = (byte)(sum&0xFF);
        cmd[index++] = 0x16;

        return cmd;
    }

    //处理接收到的水表点钞数据
    private void ProcessDC(byte[] rec)
    {
        if (null == rec)
        {
            return;
        }

        //提取电表的序列号
        byte[] bytSN = getDevCode_byt(rec);
        String sn = CommonFun.bcd2Str(bytSN);

        //采集电池电压
        int index = 64;
        int nVol = rec[index++]&0xFF;
        nVol &= 0xFF;
        float fBat = nVol+200;
        fBat /= 100;

        //表的测量数据
        index = 74;
        nVol = rec[index++];

        //用水脉冲量,4个字节
        int nTotalWater = rec[index++];
        nTotalWater <<= 8;
        nTotalWater += rec[index++];
        nTotalWater <<= 8;
        nTotalWater += rec[index++];
        nTotalWater <<= 8;
        nTotalWater += rec[index++];

        //提取单位
        int nWeight = rec[index++];


        //根据单位换算成实际立方量
        float fWeight = 0.001f;
        switch (nWeight)
        {
            case  1:
            {
                fWeight = 0.001f;
                break;
            }
            case  2:
            {
                fWeight = 0.01f;
                break;
            }
            case  3:
            {
                fWeight = 0.1f;
                break;
            }
            case  4:
            {
                fWeight = 1.0f;
                break;
            }
            case 5:
            {
                fWeight = 10.0f;
                break;
            }
            default:
            {
                break;
            }
        }

        //计算总的用水量
        float fTotal = nTotalWater;
        fTotal *= fWeight;

        //获取设备属性
        String sDevName = "WMeter"+sn;
        IDevice device = ProductionContainer.getTheMeterDeviceContainer().getDevice(sDevName);
        if (null != device)
        {
            IProductProperty property = device.getProperty("total_water");
            if (null != property)
            {
                float fValue = fTotal;
                fTotal *= 100.0f;
                int nTotal = (int)fTotal;
                property.setValue(nTotal);

                //保存到Redis数据库中
                String sKey = sDevName+"-total_water";
                String sValue = String.format("%f",fValue);
                MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);

                sKey = sDevName+"-fresh_tick";
                sValue = String.format("%d",CommonFun.GetTick());
                MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);

            }

            property = device.getProperty("bat_vol");
            {
                float fValue = fBat;
                fBat *= 100.0f;
                int nBat = (int)fBat;
                property.setValue(nBat);

                String sKey = sDevName+"-bat_vol";
                String sValue = String.format("%f",fValue);
                MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
            }


        }


        //接收到水表数据
        System.err.println("处理水表点抄数据: " + LocalDateTime.now());
    }

    @Override
    public boolean startChannel() {

        try
        {

            thread_listen = new WMeterChannelListenThread(this);
            thread_listen.start();


            System.out.println("****水表数据采集服务器端已经启动");
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            err  ="";

            return false;
        }

        return true;

    }

    private void SetupPort()
    {
        if(!has_started)
        {
            has_started = true;

            // 1.创建服务器端DatagramSocket，指定端口
            try
            {
                server_udp_socket = new DatagramSocket(6100);
            }
            catch (Exception ex)
            {

            }

        }
    }


    //侦听水表的数据,接收到后马上发送点钞数据,并定时重启防止端口号死机
    @Override
    public void RecThreadFun() {

        while (true)
        {
            //启动端口
            SetupPort();

            //接收到水表数据，则新开线程进行数据查询
            try
            {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data,data.length);
                server_udp_socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞

                int nLen = packet.getLength();
                if (packet.getLength() > 0)
                {
                    byte[] rec = new byte[nLen];
                    for(int i = 0; i < nLen; i++)
                    {
                        rec[i] = data[i];

                    }

                    //根据不同的数据帧类型做不同的处理
                    int nFramType= getFrameType(rec);
                    String sType = String.format("%d",nFramType);
                    System.out.println("****水表数据类型"+sType);
                    if (nFramType == 0x02)         //上报通知帧
                    {
                        byte[] devCode = getDevCode_byt(rec);
                        WMeterSendThread udpServerThread = new WMeterSendThread(packet,this,devCode);
                        udpServerThread.start();
                    }
                }

                Thread.sleep(30);
            }
            catch (Exception ex)
            {
                System.out.println("****侦听UDP6100端口超时");
            }
        }

    }

    @Override
    public void stopChannel() {

    }

    //处理发送点抄数据
    @Override
    public void SendThreadFun() {

    }

    //提取设备号

    //处理点抄数据
    public void ProcessNewCall(DatagramPacket packet,byte[] devCode)
    {

        if (null == packet)
        {
            return;
        }

        //生成点钞命令
        byte[] cmd =	getSendCmd(devCode);

        //根据水表的UDP连接创建数据发送Packet
        DatagramPacket datagramPacket2 =  new DatagramPacket(cmd, cmd.length, packet.getAddress(),packet.getPort());

        //发送给水表
        try {
            server_udp_socket.send(datagramPacket2);
        } catch (IOException e) {
            System.out.println("****水表点钞命令发送异常"+e.getMessage());
        }

        //等待水表回复

        try
        {
            byte[] rec =	new byte[1024];
            DatagramPacket dpg =  new DatagramPacket(rec, rec.length, packet.getAddress(),packet.getPort());
            server_udp_socket.setSoTimeout(15000);
            server_udp_socket.receive(dpg);


            int nLen = dpg.getLength();
            if (nLen > 0) {
                byte[] recData = new byte[nLen];
                for (int i = 0; i < nLen; i++) {
                    recData[i] = rec[i];
                }

                ProcessDC(recData);
            }
        }
        catch (Exception ex)
        {
            String sErr = ex.getMessage();
            sErr = "";

            System.out.println("****等待响应数据超时"+ex.getMessage());
        }

    }





    @Override
    public int addGather(IDeviceGather gather) {
        return 0;
    }

    @Override
    public int detachGather(IDeviceGather gather) {
        return 0;
    }

    @Override
    public boolean addFilter(IChannelFilter filter) {
        return false;
    }

    @Override
    public void detachFilter(IChannelFilter filter) {

    }
}
