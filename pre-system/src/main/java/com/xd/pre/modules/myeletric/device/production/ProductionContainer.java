package com.xd.pre.modules.myeletric.device.production;

import com.xd.MqttMsg;
import com.xd.MyMqttStub;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.channel.ChannelContainer;
import com.xd.pre.modules.myeletric.device.channel.IMyChannel;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyProductFunctionParamQry;
import com.xd.pre.modules.myeletric.mapper.*;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.myeletric.vo.MyWMeterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

//产品容器
public class ProductionContainer implements Runnable {

    public static final int MAX_METER = 50000;

    private boolean isWorking = false;

    private boolean has_started = false;


    //任务线程
    private Thread thread_task = null;


    @Autowired
    private MyProductInfoMapper  infoMapper ;

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
    private StringRedisTemplate redisTemplate;

    //产品映射表
    private HashMap<String,IProduct> map_product = new HashMap<String,IProduct>();

    //设备映射表，产品不能有名称重复
    private HashMap<String,IDevice> map_device = new HashMap<String,IDevice>();

    //产品队列,用于加速循环处理回调函数,检查Mapper的查找效率，同时用于保证修改的同步性
    private List<IProduct> lst_Product = new ArrayList<IProduct>();

    //数据采集器集合
    List<IDeviceGather> lst_gather = new ArrayList<IDeviceGather>();

    //新的数据采集器请求注册队列，需要多线程同步
    private LinkedBlockingQueue<IDeviceGather> registe_gather_queue = new LinkedBlockingQueue<IDeviceGather>();

    //单件对象
    public static ProductionContainer sinTon = null;

    //设置Mapper
    public void SetMapper(MyProductInfoMapper productMapper,
                          MyProductPropertyMapper propertyMapper,
                          MyProductSignalMapper signalMapper1,
                          MyProductEventMapper eventMapper1,
                          MyProductFunctionMapper functionMapper1,
                          MyProductFunctionParamMapper functionParamMapper1,
                          MyProductDeviceMapper deviceMapper1,
                          StringRedisTemplate redis)
    {
        infoMapper = productMapper;
        productPropertyMapper = propertyMapper;
        signalMapper = signalMapper1;
        eventMapper = eventMapper1;
        functionMapper = functionMapper1;
        functionParamMapper = functionParamMapper1;
        deviceMapper = deviceMapper1;
        redisTemplate = redis;
    }

    //获取单件对象
    public static  ProductionContainer getTheMeterDeviceContainer()
    {
        if (null == sinTon)
        {
            sinTon = new  ProductionContainer();
        }

        return  sinTon;
    }

    public  boolean IsWorking()
    {
        return  isWorking;
    }

    //判断设备类型是否为预付费
    public boolean IsPrechargeDevice(String sProductName)
    {
        if (sProductName.equals("MY610-ENB"))
        {
            return true;
        }
        else if (sProductName.equals("MY630-ENB"))
        {
            return true;
        }
        else if (sProductName.equals("MY610-E"))
        {
            return true;
        }
        else if (sProductName.equals("MY630-E"))
        {
            return true;
        }
        else if (sProductName.equals("MY630-ENB-CT"))
        {
            return true;
        }

        return false;
    }

    //设置电度值
    public  void FreshTotalEpFroRedis(String devName,float fValue)
    {
        String sKey = devName+"-total_ep";
        String sValue = String.format("%f",fValue);
       // redisTemplate.opsForValue().set(sKey,sValue);
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
    }

    //设置剩余电度值
    public  void FreshLeftEpFroRedis(String devName,float fValue)
    {
        String sKey = devName+"-left_ep";
        String sValue = String.format("%f",fValue);
      //  redisTemplate.opsForValue().set(sKey,sValue);
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
    }
    //设置刷新电度时间
    public  void FreshTickFroRedis(String devName,int nTick)
    {
        String sKey = devName+"-fresh_tick";
        String sValue = String.format("%d",nTick);
        //redisTemplate.opsForValue().set(sKey,sValue);
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(sKey,sValue);
    }

    //读取电度数据和刷新时间
    public boolean FetchMeterEP(MyMeterVo meter)
    {
        if (null == meter)
        {
            return  false;
        }

        //读取累计电度
        String devName = "Meter"+String.format("%06d",meter.getMeter_id());
        String sKey = devName+"-total_ep";
        String sValue = "";
        float fValue = 0.0f;
        try
        {
            sValue=redisTemplate.opsForValue().get(sKey).toString();
            fValue = Float.parseFloat(sValue);
            meter.setCur_ep(fValue);
        }
        catch (Exception ex)
        {

        }

        try
        {
            sKey = devName+"-left_ep";
            sValue=redisTemplate.opsForValue().get(sKey).toString();
            fValue = Float.parseFloat(sValue);
            meter.setLeft_ep(fValue);
        }
        catch (Exception ex)
        {

        }


        try
        {
            sKey = devName+"-fresh_tick";
            sValue=redisTemplate.opsForValue().get(sKey).toString();
            int nValue = Integer.parseInt(sValue);
            meter.setFresh_tick(nValue);
        }
        catch (Exception ex)
        {

        }



        return  true;
    }

    //读取用水量数据和刷新时间
    public boolean FetchMeterWater(MyWMeterVo wmeter)
    {
        if (null == wmeter)
        {
            return  false;
        }

        //读取累计电度
        String devName = "WMeter"+wmeter.getMeter_sn();
        String sKey = devName+"-total_water";
        String sValue = "";
        float fValue = 0.0f;
        try
        {
            sValue=redisTemplate.opsForValue().get(sKey).toString();
            fValue = Float.parseFloat(sValue);
            wmeter.setCur_water(fValue);
        }
        catch (Exception ex)
        {

        }

        try
        {
            sKey = devName+"-fresh_tick";
            sValue=redisTemplate.opsForValue().get(sKey).toString();
            int nValue = Integer.parseInt(sValue);
            wmeter.setFresh_tick(nValue);
        }
        catch (Exception ex)
        {

        }



        return  true;
    }

    public synchronized List<IProduct> getAllITem()
    {
        List<IProduct> lst = new ArrayList<>();
        lst_Product.forEach(e->
        {
            lst.add(e);
        });

        return lst;
    }


    //添加产品
    public  synchronized int   AddProduct(IProduct product)
    {
        if (null ==product)
        {
            return 1;
        }

        if (getProduct(product.getProduct_name()) != null)
        {
            return 2;
        }

        map_product.put(product.getProduct_name(),product);

        //刷新列表
        lst_Product.clear();
        Iterator iter = map_product.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            lst_Product.add((IProduct)entry.getValue());
        }

        return 0;
    }



    //获取设备
    public  IDevice getDevice(String sDevice)
    {

        if (map_device.containsKey(sDevice))
        {
            return map_device.get(sDevice);
        }

        return null;
    }

    //获取产品
    public IProduct getProduct(String productName)
    {
        IProduct product = null;
        product = map_product.get(productName);
        return product;
    }

    //创建产品的属性
    public boolean CreateProductProperty(IProduct product)
    {
        if (null == product)
        {
            return false;
        }

        List<MyProductPropertyInfo> lst = productPropertyMapper.getProductProperty(product.getProduct_name());
        lst.forEach(e->{

            IProductProperty productProperty = new MyProductProperty(e);
            product.AddProperty(productProperty);

        });

        return true;
    }

    //创建产品的信号
    public boolean CreateProductSignal(IProduct product)
    {
        if (null == product)
        {
            return false;
        }

        List<MyProductSignalInfo> lst = signalMapper.getProductSignal(product.getProduct_name());
        lst.forEach(e->{

            IProductSignal signal = new MyProductSignal(e);
            product.AddSignal(signal);

        });

        return true;
    }

    //创建产品的事件配置
    public boolean CreateProductEvent(IProduct product)
    {
        if (null == product)
        {
            return false;
        }

        List<MyProductEventInfo> lst = eventMapper.getProductEvent(product.getProduct_name());
        lst.forEach(e->{

            MyProductEvent event = new MyProductEvent(e);
            product.AddEvent(event);

        });

        return true;
    }
    //创建函数的参数
    public boolean CreateFunctionParam(IProductFunction iFunction)
    {
        if (null == iFunction)
        {
            return  false;
        }
        MyProductFunctionParamQry qry = new MyProductFunctionParamQry();
        qry.setProduct_name(iFunction.getProductName());
        qry.setFunction_name(iFunction.getFunctionName());
        List<MyProductFunctionParamInfo> lst = functionParamMapper.getProductFunctionParam(qry);
        lst.forEach(e->{

            MyProductFunctionParam functionParam = new MyProductFunctionParam(e);

            //添加函数的参数
            iFunction.addParam(functionParam);

        });

        return true;
    }

    //创建产品的函数
    public boolean CreateFunction(IProduct product)
    {
        if (null == product)
        {
            return false;
        }

        List<MyProductFunctionInfo> lst = functionMapper.getProductFunction(product.getProduct_name());
        lst.forEach(e->{

            IProductFunction function = new MyProductFunction(e);
            CreateFunctionParam(function);

            //创建函数的参数
            product.AddFunction(function);

        });

        return true;
    }

    //根据产品信息创建产品
    public IProduct CreateProduct(MyProductInfo productInfo)
    {
        if(null == productInfo)
        {
            return null;
        }

        IProduct product = new MyProduct(productInfo);
        CreateProductProperty(product);
        CreateProductSignal(product);
        CreateFunction(product);
        CreateProductEvent(product);
        AddProduct(product);
        LoadDevice(product);

        //创建数据网关设备
        product.CreateGatewayGather();

        return product;
    }

    //装载产品所有的设备
    private void LoadDevice(IProduct product)
    {
        if(null == product)
        {
            return ;
        }

        List<MyProductDeviceInfo> lst = deviceMapper.getProductDevice(product.getProduct_name());
        int nLen = lst.size();
        for(int i = 0; i < nLen; i++)
        {
            MyProductDeviceInfo deviceInfo = lst.get(i);
            if (null != deviceInfo)
            {
                product.AddDevice(deviceInfo);
            }
        }

    }



    //装载产品
    private void LoadProduct()
    {
        List<MyProductInfo> lstProduct = infoMapper.getAllProduct();
        lstProduct.forEach(e->
        {
            if (null == e)
            {
                return;
            }

           CreateProduct(e);
        });
    }

    //添加设备到映射表
    public void AddDeviceToMap(IDevice device)
    {
        if (null == device)
        {
            return;
        }

        map_device.put(device.getDeviceName(),device);
    }

    //注册数据采集器
    public  void RegisteGather(IDeviceGather gather)
    {
        if(null == gather)
        {
            return;
        }

        IDevice device = gather.getGateWayDevice();
        if (null == device)
        {
            return;
        }

        //加入注册请求队列，然后有回调线程去注册，保证了数据采集器集合的同步性
        try
        {
            registe_gather_queue.put(gather);
        }
        catch (Exception ex)
        {

        }


    }

    //通过网关名称获取网关设备
    public IDeviceGather getGather(String gatherName)
    {
        IDeviceGather gather = null;
        int nCount = lst_gather.size();

        for(int i = 0; i < nCount; i++)
        {
            try{
                gather = lst_gather.get(i);
                if (null != gather)
                {
                    IDevice device = gather.getGateWayDevice();
                    if (null != device && device.getGatewayName().equals(gatherName))
                    {
                        return gather;
                    }
                }

            }
            catch (Exception ex)
            {
                return null;
            }
        }

        return  null;
    }

    //将子设备挂接到网关设备中
    public void LoadGatherSubDevice(IDeviceGather gather)
    {
        if (null == gather)
        {
            return;
        }
        IDevice gatherDevice = gather.getGateWayDevice();
        if (null == gatherDevice)
        {
            return;
        }
        String gatherName = gatherDevice.getDeviceName();

        int nCount = lst_Product.size();
        for(int i = 0; i < nCount; i++)
        {
            IProduct product = lst_Product.get(i);
            if (null != product)
            {
                List<IDevice> lstDevice = product.getAllDevice();

                for(int j = 0; j < lstDevice.size(); j++)
                {
                    IDevice device = lstDevice.get(j);
                    if (null != device && device.getGatewayName().equals(gatherName))
                    {
                        gather.AddNewSubDevice(device);
                    }

                }
            }

        }
    }

    //启动线程，回调容器里面的每个设备
    public boolean StartService()
    {
        String topic = "";

        if (has_started)
        {
            return false;
        }

        //判断数据库处理对象是否正常初始化
        if (infoMapper == null
                || eventMapper == null
                || productPropertyMapper ==null
                || signalMapper == null)
        {
            return  false;
        }

        has_started  =true;

        //从数据库中装载产品
        LoadProduct();

        //启动工作线程
        isWorking = true;
        thread_task = new Thread(this);
        thread_task.start();

        return  true;
    }

    //将数据保存到Redisd的回调函数
    private void SaveRedisTick()
    {

    }

    //定时刷新电表

    @Override
    public void run() {

        while (isWorking)
        {
            //调用系统缓存
            MySystemRedisBuffer.getTheSinTon().CallTick();

            try
            {
                Thread.sleep(100);
            }
            catch (Exception ex)
            {

            }

            //调用每个Device的网络检测
            int nLen = lst_Product.size();
            for(int i = 0; i < nLen; i++)
            {
                IProduct product = lst_Product.get(i);
                if (null == product)
                {
                    List<IDevice> lstDevice  = product.getAllDevice();
                    int nDeviceCount = lstDevice.size();
                    for( int j = 0; j < nDeviceCount; j++)
                    {
                        IDevice device = lstDevice.get(j);
                        if (null != device)
                        {
                            device.OnLineCheck();
                        }
                    }
                }
            }

            //提取新注册的数据采集器队列，加入通道中和队列中
            while(registe_gather_queue.size() > 0)
            {
                IDeviceGather newGather = registe_gather_queue.poll();
                if (null != newGather)
                {
                    try
                    {
                        //装载物联网关设备的所有子设备
                        LoadGatherSubDevice(newGather);

                        //将物联网关设备挂接到通讯通道下
                        IDevice device = newGather.getGateWayDevice();
                        if (null != device)
                        {
                            IMyChannel channel = ChannelContainer.getChannelContainer().getChannel(device.getChannelName());
                            if (null != channel)
                            {
                                channel.addGather(newGather);
                                lst_gather.add(newGather);
                            }
                        }

                    }
                    catch (Exception ex)
                    {

                    }

                }
            }

            //循环回调每个数据采集器
            for(int i = 0; i < lst_gather.size(); i++)
            {
                IDeviceGather gather = lst_gather.get(i);
                if (null != gather)
                {
                    gather.callTick();
                }
            }


        }
    }
}
