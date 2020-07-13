package com.xd.pre.modules.myeletric.task;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.channel.ChannelContainer;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProduct;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.mapper.*;
import com.xd.pre.modules.myeletric.message.MyMessageContainer;
import com.xd.pre.modules.myeletric.service.*;
import com.xd.pre.modules.pay.PaymentContainer;
import com.xd.pre.modules.pay.mapper.MyPaymentMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class MyEPWaterRecordTask {

    //上一次保存数据的时间
    private  DateTime m_tmLastSave = DateTime.now();
    private  boolean  m_bInited = false;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IMyMeterService iMyMeterService;

    @Autowired
    private IMyWMeterService iMyWMeterService;

    @Autowired
    private IMyRoomService iMyRoomService;


    @Autowired
    private MyRoomTenantMapper myRoomTenantMapper;

    @Autowired
    private MyProductInfoMapper infoMapper ;

    @Autowired
    private MyProductPropertyMapper productPropertyMapper ;

    @Autowired
    private MyProductSignalMapper signalMapper ;

    @Autowired
    private MyProductEventMapper eventMapper ;

    @Autowired
    private MyProductFunctionMapper functionMapper ;

    @Autowired
    private MyProductFunctionParamMapper functionParamMapper ;

    @Autowired
    private MyProductDeviceMapper deviceMapper ;

    @Autowired
    private MyProductPropertyRecordMapper  productPropertyRecordMapper ;


    @Autowired
    private MyMqttChannelMapper myMqttChannelMapper ;

    @Autowired
    private MyChannelInfoMapper myChannelInfoMapper ;

    @Autowired
    private MyPaymentMapper ipaymentMapper ;

    @Autowired
    private IMyMeterFeeService myMeterFeeService;

    @Autowired
    private IWaterFeeService myWaterFeeService;



    //每个整点执行数据保存
    @Scheduled(cron = "0 0 0/1 * * ?")
   // @Scheduled(cron="0/30 * *  * * ? ")  //60秒一次数据保存
    private void configureTasks() {

        //记录属性数据
        RecordProperty();

        //记录属性数据
          RecordEp();

        //用水抄表
         RecordEWater();

        CreateFee();

        System.err.println("执行保存数据任务时间: " + LocalDateTime.now());
    }
    //每个整点执行数据保存
  //  @Scheduled(cron = "0 0 0/1 * * ?")
    @Scheduled(cron="0/30 * *  * * ? ")  //60秒一次数据保存
    private void recordTasks() {

        //装载通道数据
        if(!m_bInited)
        {
            m_bInited = true;
            ChannelContainer.getChannelContainer().setMapper(myChannelInfoMapper,myMqttChannelMapper);
            ChannelContainer.getChannelContainer().loadChannel();
            ChannelContainer.getChannelContainer().startService();

            //设置系统缓存
            MySystemRedisBuffer.getTheSinTon().setRedis(redisTemplate);

            //装载产品数据
            ProductionContainer.getTheMeterDeviceContainer().SetMapper(
                    infoMapper,
                    productPropertyMapper,
                    signalMapper,
                    eventMapper,
                    functionMapper,
                    functionParamMapper,
                    deviceMapper,
                    redisTemplate);
            ProductionContainer.getTheMeterDeviceContainer().StartService();

            //设置支付单容器
            PaymentContainer.GetThePaymentContainer().setMapper(
                    ipaymentMapper,
                    myMeterFeeService,
                    myWaterFeeService);
            PaymentContainer.GetThePaymentContainer().StartService();

            //启动微信通知
            MyMessageContainer.getSinTon().StartService();

            //将电表和水表进行设备绑定，方便查询
            System.err.println("执行启动任务时间: " + LocalDateTime.now());
        }
    }

    //将设备属性数据按时序进行序列化保存
    private void RecordProperty()
    {
        List<IProduct> lstProduct =   ProductionContainer.getTheMeterDeviceContainer().getAllITem();
        for(int i = 0; i < lstProduct.size(); i++)
        {
            IProduct product = lstProduct.get(i);
            if (null == product)
            {
                continue;
            }

            List<IDevice> lstDevice = product.getAllDevice();
            for(int j = 0; j < lstDevice.size(); j++)
            {
                IDevice device = lstDevice.get(j);

                List<IProductProperty> lstProperty = device.getPropertys();
                for(int k = 0; k< lstProperty.size(); k++)
                {
                    IProductProperty property = lstProperty.get(k);
                    if (null == property )
                    {
                        continue;
                    }
                    MyPropertyRecord record = new MyPropertyRecord();
                    record.setProduct_name(property.getProductName());
                    record.setDevice_name(property.getDevName());
                    record.setProperty_name(property.getPropertyName());
                    record.setProperty_value((float)property.getValue());
                    record.setIs_valid(1);
                    record.setSave_tick(CommonFun.GetTick());
                    productPropertyRecordMapper.recordProperty(record);
                }
            }
        }
    }


    //记录用电和用水数据
    private  void RecordEp()
    {

        List<MyMeter> lstMeter =   iMyMeterService.getAllMeterList();
        for(int i = 0; i < lstMeter.size(); i++)
        {
            MyMeter meter = lstMeter.get(i);
            if (null == meter)
            {
                continue;
            }

            //在Redis中获取电表当前的读数
            MyMeterRecord meterRecord = new MyMeterRecord();
            meterRecord.setMeter_id(meter.getMeter_id());
            meterRecord.setRoom_id(meter.getRoom_id());
            meterRecord.setEp_cur(0);
            meterRecord.setFresh_time(new Timestamp(System.currentTimeMillis()));

            //记录Property中的total_ep,用Meter+6位表号为设备名称查找设备
            //ProductName则采用MeterType作为名称
            String ProductName = meter.getMeter_type();
            String DeviceName = "Meter"+String.format("%06d",meter.getMeter_id());

            IProduct product = ProductionContainer.getTheMeterDeviceContainer().getProduct(ProductName);
            if (null != product)
            {
                IDevice device = product.getDevice(DeviceName);
                if (null != device)
                {
                    IProductProperty property = device.getProperty("total_ep");
                    if (property != null && property.IsPropertyValid())   //数据有效才能记录
                    {
                        float fValue = property.getFloatValue();
                        meterRecord.setEp_cur(fValue);


                        //保存读数同时刷新上期读数
                        iMyMeterService.saveEP(meter,meterRecord);


                    }
                }
            }

        }


    }

    //保存水费数据
    private  void RecordEWater()
    {
        List<MyWMeter> lstMeter =   iMyWMeterService.getAllMeterList();
        for(int i = 0; i < lstMeter.size(); i++)
        {
            MyWMeter meter = lstMeter.get(i);
            if (null == meter)
            {
                continue;
            }

            //在Redis中获取电表当前的读数
            MyWMeterRecord meterRecord = new MyWMeterRecord();
            meterRecord.setMeter_id(meter.getMeter_id());
            meterRecord.setRoom_id(meter.getRoom_id());
            meterRecord.setWater_cur(0);
            meterRecord.setFresh_time(new Timestamp(System.currentTimeMillis()));

            //记录Property中的total_ep,用Meter+6位表号为设备名称查找设备
            String ProductName = meter.getMeter_type();
            String DeviceName = "WMeter"+meter.getMeter_sn();

            IProduct product = ProductionContainer.getTheMeterDeviceContainer().getProduct(ProductName);
            if (null != product)
            {
                IDevice device = product.getDevice(DeviceName);
                if (null != device)
                {
                    IProductProperty property = device.getProperty("total_water");
                    if (property != null && property.IsPropertyValid())   //数据有效才能记录
                    {
                        float fValue = property.getFloatValue();
                        meterRecord.setWater_cur(fValue);


                        //保存读数同时刷新上期读数
                        iMyWMeterService.recordWater(meter,meterRecord);


                    }
                }
            }

        }



        /*List<MyWMeter> lstWMeter =   iMyWMeterService.getAllMeterList();
        lstWMeter.forEach(meter->{

            //在Redis中获取水表当前的读数
            MyWMeterRecord meterRecord = new MyWMeterRecord();
            meterRecord.setMeter_id(meter.getMeter_id());
            meterRecord.setRoom_id(meter.getRoom_id());
            meterRecord.setWater_cur(0);
            meterRecord.setWater_last(0);
            meterRecord.setWater_used(0);
            meterRecord.setFresh_time(new Timestamp(System.currentTimeMillis()));

            String sKey = "WaterMeter"+String.format("%06d",meter.getMeter_id());
            if (redisTemplate.hasKey(sKey))
            {
                try
                {
                    float fWater_Used = 0.0f;
                    String str_water_cur= (String)redisTemplate.opsForHash().get(sKey,"Total");
                    float f_water_cur = Float.valueOf(str_water_cur)/100.0f;

                    //数据刷新的Tick
                    String sFreshTick = (String)redisTemplate.opsForHash().get(sKey,"Tick");
                    Timestamp tmFresh = Timestamp.valueOf(sFreshTick);
                    long nFreshTick = tmFresh.getTime()/1000;

                    //判断采样时间是否为一天之内
                    long lNowTick = System.currentTimeMillis()/1000;
                    long nDelt = lNowTick > nFreshTick?lNowTick-nFreshTick:nFreshTick-lNowTick;

                    //24小时的采集数据才保存
                    if (nDelt > 3600*24)
                    {

                        //  return;     //在foreach中return跟普通的continue是一样
                    }

                    //一天之内采集的值缓存
                    float f_water_last = meter.getWater_last();
                    fWater_Used = f_water_cur > f_water_last ?f_water_cur-f_water_last:0.0f;

                    //刷新当前的电度数据和
                    meterRecord.setWater_cur(f_water_cur);
                    meterRecord.setWater_last(meter.getWater_last());
                    meterRecord.setWater_used(fWater_Used);


                    //保存读数同时刷新上期读数
                    iMyWMeterService.recordWater(meter,meterRecord);
                }
                catch (Exception ex)
                {
                    int llk = 0;
                }
            }


        });*/
    }

    //自动生成水费单
    private  void CreateFee()
    {
        //获取所有的租赁记录
       Timestamp tmNow = new Timestamp(System.currentTimeMillis());
        List<MyRoomTenant> tenantLst = myRoomTenantMapper.getAllTenant();
        if (null == tenantLst || tenantLst.size() == 0)
        {
            return;
        }

        //循环处理每个已经出租的房间，是否到达了结算周期，到达则结算
        tenantLst.forEach(roomtenant -> {

            if (null == roomtenant
                    || roomtenant.getRoom_tenant_id() == ""
                    || roomtenant.getRoom_tenant_id() == "0")
            {
                return;   //跳过，到下一个记录
            }

            //取所属期的月份，如果不一样就需要核算费用
            if (roomtenant.getPeriod_start_time().getMonth() == tmNow.getMonth())
            {
                return;
            }

            //更新所属期
            Timestamp startTime = roomtenant.getPeriod_start_time();

            //计算核算的结束时间
            int nYear = roomtenant.getPeriod_start_time().getYear();
            int nMonth = roomtenant.getPeriod_start_time().getMonth()+1;
            int nMaxDay = CommonFun.getDaysByYearMonth(nYear,nMonth);
            DateTime tm = new DateTime(nYear+1900,nMonth,nMaxDay,23,59,59);
            //Timestamp timeEnd =  new Timestamp(nYear,nMonth,nMaxDay,23,59,59,999);
            Timestamp timeEnd = new Timestamp(tm.getMillis());


            //创建新的收费单
            String sErr = "";
            iMyRoomService.CreateRoomBill(roomtenant,startTime,timeEnd,sErr);

        });
    }
}
