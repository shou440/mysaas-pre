package com.xd.pre.modules.myeletric.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.command.CommandContainer;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.*;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import com.xd.pre.modules.myeletric.service.*;
import com.xd.pre.modules.myeletric.vo.CommandSN;
import com.xd.pre.modules.myeletric.vo.CommandStateVO;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.pay.IPayment;
import com.xd.pre.modules.pay.PaymentContainer;
import com.xd.pre.modules.pay.PaymentInfo;
import com.xd.pre.modules.pay.WXPayment;
import com.xd.pre.modules.pay.dto.PaymentQryDto;
import com.xd.pre.modules.pay.dto.PaymentStateDto;
import com.xd.pre.modules.pay.mapper.MyPaymentMapper;
import com.xd.pre.modules.sys.domain.SysUserCount;
import com.xd.pre.modules.sys.mapper.SysUserCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.weixin4j.model.pay.OrderQueryResult;
import org.weixin4j.model.pay.WCPay;
import org.weixin4j.model.user.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//处理微信端用户的请求

@RestController
@RequestMapping("/weixin")
public class MyWeixinUserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IMyAreaService iMyAreaService;

    @Autowired
    private IMyRoomService iMyRoomService;

    @Autowired
    private IMyMeterFeeService iMyMeterFeeService;

    @Autowired
    private IWaterFeeService iWaterFeeService;

    @Autowired
    private MyPaymentMapper paymentMapper;

    @Autowired
    private IMyMeterService iMyMeterService;

    @Autowired
    private IMyRoomService myRoomService;


    @Autowired
    private SysUserCountMapper sysUserCountMapper;

    //通过网页授权的Code获取微信用户信息
    @RequestMapping(value = "/getwxuserinfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfo( MyWXUserFilterDto filter) {

        //获取code
        String sCode = filter.getCode();
        String uuid = filter.getParam();

        //获取当前连接的用户ID,先将code换成Access_Token
        try {

            User user = MyWeixinStub.getTheMyWeixinStub().GetUserInfo(sCode);
            String ret = JSON.toJSONString(user);

            //将用户信息记录到Redis缓冲中
            redisTemplate.opsForValue().set(uuid,ret);

            return R.ok(ret);
        }
        catch (Exception ex) {

            return R.error("获取微信用户信息失败"+ex.getMessage()+"\n");
        }
    }

    //通过uuid获取微信认证用户的信息
    @RequestMapping(value = "/getwxuserinfobyuuid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfobyUUID( MyWXUserFilterDto filter) {

        //获取code
        String uuid = filter.getParam();

        //获取当前连接的用户ID
        try {

            //将用户信息记录到Redis缓冲中
            String userinfo = redisTemplate.opsForValue().get(uuid);
            if (userinfo != null)
            {
                return R.ok(userinfo);
            }
            else
            {
                return R.error("获取用户微信信息为空");
            }

        } catch (Exception ex) {

            return R.error("获取微信用户信息失败");
        }
    }

    @RequestMapping(value = "/getmetersbytenantopenid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMeterByTenantOpenid(MyMeterFilter filter) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyMeter> list=iMyMeterService.getMeterByTenantOpenid(filter.getTenant_openid());
        for(int i = 0; i < list.size(); i++)
        {
            MyMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }

            MyMeterVo item = new MyMeterVo(meter);
            MyRoom room = myRoomService.getRoomByID(meter.getRoom_id());
            if (null != room)
            {
                item.setRoom_name(room.getRoom_name());
            }

            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);
            listOut.add(item);
        }

        return R.ok(listOut);
    }

    @RequestMapping(value = "/getmeterbyid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMeterByMeterID(MyMeterFilter filter) {

        System.out.print("根据ID获取电表数据1\n");

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyMeter> list=iMyMeterService.getMeter(filter.getMeter_id());
        if (list == null || list.size() == 0)
        {
            return R.error("获取电表信息失败!");
        }
        System.out.print("根据ID获取电表数据2\n");
        MyMeter meter = list.get(0);
        if (null == meter)
        {
            return R.error("获取电表信息失败!");
        }
        System.out.print("根据ID获取电表数据3\n");
        MyMeterVo item = new MyMeterVo(meter);
        MyRoom room = myRoomService.getRoomByID(meter.getRoom_id());
        if (null != room)
        {
            item.setRoom_name(room.getRoom_name());
        }

        //通过Device获取实时数据
        ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);

        return R.ok(item);
    }

    //通过uuid获取微信认证用户的信息
    @RequestMapping(value = "/crtunifiledorder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R createPayment( MyUnifiedOrderDto unifiedorderdto ) {

        //获取当前连接的用户ID
    /*    try {

            //创建支付订单
            String openid = unifiedorderdto.getOpenid();
            String paymentid = unifiedorderdto.getPaymentid();
            String message = unifiedorderdto.getMessage();
            float fFee = unifiedorderdto.getFFee();
            WCPay ret = MyWeixinStub.getTheMyWeixinStub().getUnifiedOrder(openid,paymentid,message,fFee);
            if (ret != null)
            {
                //预支付申请成功后，创建支付单的电子单据,并保存到数据库中，并添加支付单到系统缓冲
                IPayment pay = new WXPayment(openid,paymentid,message,fFee);
                if (pay.Save())
                {
                    PaymentContainer.GetThePaymentContainer().addPayment(pay);
                    return R.ok(ret);
                }


                return R.ok(ret);
            }
            else
            {
                return R.error("生成支付单失败!");
            }

        } catch (Exception ex) {

            return R.error("生成支付单失败!");
        }*/
     return R.ok("暂不支持");
    }


    //租户通过微信获取登录获取租户的电费单
    @RequestMapping(value = "/gettenantmeterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getmeterfeeByTenant( MyTenantFeeQueryDto queryParam ) {

        //获取当前连接的用户ID
        try {

            //创建支付订单
            if (null == queryParam)
            {
                return R.error("查询参数错误!");
            }

            List<MyMeterFee> meterFeeLst = iMyMeterFeeService.getMeterFeeByTenantOpenid(queryParam);

            return R.ok(meterFeeLst);

        } catch (Exception ex) {

            return R.error("下载租户电费失败!");
        }
    }

    //租户通过微信获取登录获取租户的水费单
    @RequestMapping(value = "/gettenantwaterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getwaterfeeByTenant( MyTenantFeeQueryDto queryParam ) {

        //获取当前连接的用户ID
        try {

            if (null == queryParam)
            {
                return R.error("查询参数错误!");
            }

            List<MyWaterFee> meterFeeLst = iWaterFeeService.getWaterFeeByTenantOpenid(queryParam);

            return R.ok(meterFeeLst);

        } catch (Exception ex) {

            return R.error("下载租户水费失败!");
        }
    }

    //微信服务器通知支付结果
    @PostMapping(value = "/notifypay")
    public R Notify(@RequestBody String notifyResultXmlStr) {


        String resSuccessXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";


        try {

            JAXBContext context = JAXBContext.newInstance(OrderQueryResult.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            OrderQueryResult result = (OrderQueryResult) unmarshaller.unmarshal(new StringReader(notifyResultXmlStr));

            //检查支付是否成功


            //获取支付订单Paymentid
            String paymentid = result.getOut_trade_no();


            //查找订单号
            List<PaymentInfo> lst = paymentMapper.getPaymentByID(paymentid);
            if (null != lst && lst.size() > 0)
            {
                PaymentInfo payInfo = lst.get(0);
                if (null != payInfo)
                {

                }
            }


            return R.ok(resSuccessXml);
        } catch (JAXBException ex) {
            return R.ok("Failed");
        }


    }
        //微信端获取费单的预支付单号
    @PostMapping(value = "/crtfeeprepay")
    public R createFeePrepay(@RequestBody String feePrepayStr) {

        //获取当前连接的用户ID
        try {

            System.err.println("创建支付单: " + feePrepayStr);

            //从Json字符串中提取费单流水号,并获取相应的水费单和电费单
            JSONObject feePrepayJson = JSON.parseObject(feePrepayStr);
            if (null == feePrepayJson)
            {
                return R.error("费单数据错误!");
            }

            String openid = feePrepayJson.getString("openid");
            JSONArray jArr = feePrepayJson.getJSONArray("fee_list");
            int nUserID = 0;
            int nLastMeterFeeUserID = 0;
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            long nTick = stamp.getTime();



            //判断费单状态是否处于等待支付状态
            List<MyMeterFee> meterFeeLst = new ArrayList<MyMeterFee>();
            List<MyWaterFee> waterFeeLst = new ArrayList<MyWaterFee>();
            boolean isFeeInvalid = true;
            float fTotalFee = 0;
            String roomname = "";
            String tenantname="";

            for(int i = 0; i < jArr.size(); i++)
            {
                JSONObject jFee = jArr.getJSONObject(i);
                String  fee_sn = jFee.getString("fee_sn");          //流水号
                String  fee_type = jFee.getString("fee_type");      //费单类型

                //检查费单状态是否处于等待付款中
                if (fee_sn.contains("E"))     //电费单
                {
                    MyMeterFee meterFee =   iMyMeterFeeService.getMeterFeeBySn(fee_sn);
                    if (null != meterFee)
                    {
                        if (meterFee.getFee_status() == 0)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单未审核!");
                        }
                        else if (meterFee.getFee_status() == 2)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单已付款!");
                        }
                        else if (meterFee.getFee_status() == 3)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单已取消!");
                        }
                        else if (meterFee.getFee_status() == 4)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费支付锁定中!");
                        }
                        else if (meterFee.getFee_status() == 1) {
                            meterFeeLst.add(meterFee);
                            fTotalFee += meterFee.getTotal_fee();
                        }
                        else
                        {
                            return R.error(meterFee.getFee_sn()+"号电费状态错误!");
                        }

                        //检查是否为统一业主的水电费单子，是同一个业主才能一起缴费
                        nUserID = meterFee.getUser_id();
                        if (nLastMeterFeeUserID == 0)
                        {

                            nLastMeterFeeUserID = nUserID;
                        }
                        else  if (nLastMeterFeeUserID != nUserID)
                        {
                            return R.error("一次缴费只能是同一个业主的水电费");
                        }

                        //检查房间名称是否一直
                        if (roomname.equals("")) {
                            roomname = meterFee.getRoom_name();
                            tenantname = meterFee.getTenant_name();
                        }
                        else
                        {
                            if (!roomname.equals(meterFee.getRoom_name()))
                            {
                                return R.error("一次只能选择一个房间的费单缴费");
                            }
                        }
                    }
                }
                else if (fee_sn.contains("W"))  //水费单
                {
                    MyWaterFee waterFee =   iWaterFeeService.getWaterFeeBySn(fee_sn);
                    if (null != waterFee)
                    {
                        if (waterFee.getFee_status() == 0)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单未审核!");
                        }
                        else if (waterFee.getFee_status() == 2)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单已付款!");
                        }
                        else if (waterFee.getFee_status() == 3)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单已取消!");
                        }
                        else if (waterFee.getFee_status() == 4)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费支付锁定中!");
                        }
                        else if (waterFee.getFee_status() == 1) {
                            waterFeeLst.add(waterFee);
                            fTotalFee += waterFee.getTotal_fee();
                        }
                        else
                        {
                            return R.error(waterFee.getFee_sn()+"号电费状态错误!");
                        }

                        nUserID = waterFee.getUser_id();
                        if (nLastMeterFeeUserID == 0)
                        {
                            nLastMeterFeeUserID = nUserID;
                        }
                        else  if (nLastMeterFeeUserID != nUserID)
                        {
                            return R.error("一次缴费只能是同一个业主的水电费");
                        }

                        //检查房间名称是否一直
                        if (roomname.equals("")) {
                            roomname = waterFee.getRoom_name();
                            tenantname = waterFee.getTenant_name();
                        }
                        else
                        {
                            if (!roomname.equals(waterFee.getRoom_name()))
                            {
                                return R.error("一次只能选择一个房间的费单缴费");
                            }
                        }
                    }

                }

            }

            //创建预支付单
            String  outErrMsg = "";
            WCPay pay = PaymentContainer.GetThePaymentContainer().CreateFeePayment(meterFeeLst,
                    waterFeeLst,
                    openid,
                    "水电缴费",
                    fTotalFee,
                    nUserID,
                    roomname,
                    tenantname,
                    outErrMsg);
            if (null == pay)
            {
                return  R.error("创建支付单错误!");
            }



            return R.ok(pay);


        } catch (Exception ex) {

            return R.error("生成支付单失败!");
        }
    }

    //获取新的执行命令序号
    @RequestMapping(value = "/getnewcmdsn", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getNewCommandSN() {

        CommandSN cmdSN = new CommandSN(CommandContainer.getInstance().getNewSN());
        return R.ok(cmdSN);

    }

    //通知支付单取消
    @PostMapping(value = "/notifypaymentcancel")
    public R notifyPaymentCancel(@RequestBody String paymentid) {

        System.out.print("通知支付单取消，单号:"+paymentid+"\n");
        IPayment pay = PaymentContainer.GetThePaymentContainer().getPayment(paymentid);
        if (null != pay)
        {
            System.out.print("通知支付单取消1，单号:"+paymentid+"\n");
            pay.NotifyCancel();
        }

        return R.ok("");

    }


        //获取新的执行命令序号
    @RequestMapping(value = "/fetchcmdstate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R fetchCommandState(MyCommandStateQrtDto cmdSN) {

        try {

            if(null == cmdSN)
            {
                System.out.print("获取命令传递参数为空");
            }

            String sState = "";
            String key = "commandstate_"+String.format("%012d",cmdSN.getCommand_sn());
            sState = MySystemRedisBuffer.getTheSinTon().getReisItemString(key);
            if(sState.equals(""))
            {
                return R.error("查询命令失败1!");
            }

            int nState = Integer.parseInt(sState);
            String str = String.format("查询命令%s状态:%d\n",key,nState);
            System.out.print(str);

            String errkey = "command-errmsg_"+String.format("%012d",cmdSN.getCommand_sn());
            String errmsg = MySystemRedisBuffer.getTheSinTon().getReisItemString(errkey);


            CommandStateVO cmdState = new CommandStateVO();
            cmdState.setCommand_sn(cmdSN.getCommand_sn());
            cmdState.setCommand_state(nState);
            cmdState.setErr_msg(errmsg);
            return R.ok(cmdState);
        }
        catch (Exception ex)
        {
            return R.error("获取命令状态异常!"+ex.getMessage());
        }



    }

        //微信端获取预付费充值的预支付单号
    @RequestMapping(value = "/crtchargeprepay", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R createMeterChargePrepay(MeterChargeDto chargeDto) {

        //获取当前连接的用户ID
        try {

            if (null == chargeDto)
            {
                return R.error("充值参数为空");
            }

            if (chargeDto.getMeter_id() == 0)
            {
                return R.error("充值电表号错误");
            }

            if (chargeDto.getCharge_fee() <= 0)
            {
                return R.error("充值金额不能小于0");
            }

            if (chargeDto.getTenant_openid() == null
             || chargeDto.getTenant_openid().equals(""))
            {
                return R.error("充值微信号不能为空");
            }

            MyMeter meter = MyDbStub.getInstance().getMeter(chargeDto.getMeter_id());
            if (null == meter)
            {
                return R.error("充值的电表配置错误!");
            }

            if (meter.getEp_price() < 0)
            {
                return R.error("电表电价配置错误!");
            }

            //获取房间信息
            MyRoom room = MyDbStub.getInstance().getRoom(meter.getRoom_id());
            if (null == room )
            {
                return R.error("电表的房间配置信息错误!");
            }

            MyArea area= MyDbStub.getInstance().getArea(room.getArea_id());
            if (null == area)
            {
                return R.error("电表的园区配置数据错误!");
            }

            //获取设备
            String sDeviceName = "Meter"+String.format("%06d",chargeDto.getMeter_id());
            IDevice device = ProductionContainer.getTheMeterDeviceContainer().getDevice(sDeviceName);
            if (null == device)
            {
                return R.error("电表设备数据配置错误!");
            }

            //检查电表是否在线
            if (!device.IsOnline())
            {
                return R.error("电表网络未连接，充值失败!");
            }

            //提取业主结算账户
            SysUserCount userCount = MyDbStub.getInstance().GetUserCount(area.getUser_id());
            if (null == userCount)
            {
                return R.error("业主未配置收款账号,无法网络充值!");
            }


            //创建充值支付单，支付完成后执行充值命令
            WCPay pay = PaymentContainer.GetThePaymentContainer().CreateChargePayment(
                    room.getRoom_name(),
                    room.getTenant_name(),
                    userCount,
                    chargeDto,
                    meter);
            if (null == pay)
            {
                return  R.error("创建支付单错误!");
            }

            return R.ok(pay);

        } catch (Exception ex) {

            return R.error("生成支付单失败!"+ex.getMessage());
        }
    }

    //查询支付单状态
    @RequestMapping(value = "/querypaystate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R QueryPayState( PaymentStateDto qryDto ) {

        try {



            if (null == qryDto || qryDto.getPayment_id() == null)
            {
                System.out.print("查询支付单1\n");
                return R.error("查询支付单状态失败!");
            }

            //从Redis中查询订单状态
            if (!qryDto.getPayment_id().equals(""))
            {
                String key = "paymentstate_"+qryDto.getPayment_id();
                String sState= MySystemRedisBuffer.getTheSinTon().getReisItemString(key);
                if (sState.equals(""))
                {
                    System.out.print("查询支付单2\n");
                    return R.error("查询支付单状态失败!");
                }

                Integer nState = 0;
                try
                {
                    nState = Integer.parseInt(sState);
                    PaymentStateDto payStateDto = new PaymentStateDto();
                    payStateDto.setPayment_id(qryDto.getPayment_id());
                    payStateDto.setPayment_state(nState);

                    String str = String.format("支付单%s状态:%d\n",qryDto.getPayment_id(),nState);
                    System.out.print(str);

                    return R.ok(payStateDto);
                }
                catch (Exception ex)
                {
                    System.out.print("查询支付单3\n"+ex.getMessage());
                    return R.error("查询支付单状态失败!");
                }
            }


        } catch (Exception ex) {
            System.out.print("查询支付单4\n"+ex.getMessage());
            return R.ok("查询支付单状态异常:"+ex.getMessage());
        }

        return R.error("查询支付单状态失败!");
    }

    //查询支付单结果
    @RequestMapping(value = "/querypament", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R QueryPament( PaymentQryDto qryDto ) {

        try {



            if (null == qryDto || qryDto.getPayment_id() == null)
            {
                return R.error("查询账单失败!");
            }

            //如果优先指定了pamentid，则只需要查询指定账单就可以
            if (!qryDto.getPayment_id().equals(""))
            {
                 System.out.print("查询支付结果");
                  List<PaymentInfo> lst =   paymentMapper.getPaymentByID(qryDto.getPayment_id());

                  if (null != lst && lst.size() != 0)
                  {
                      PaymentInfo payInfo = lst.get(0);
                      return R.ok(payInfo);
                  }
            }

            //租户查询自己所有的记录
            if (!qryDto.getTenant_openid().equals("") && qryDto.getUser_id() == 0)
            {
                System.out.print("查询租户的支付单");
                List<PaymentInfo> lstRet = paymentMapper.getPaymentByTenant(qryDto.getTenant_openid());
                return R.ok(lstRet);
            }

            //其他需要指定时间段
            if (qryDto.getStart_time() == null || qryDto.getEnd_time() == null)
            {
                return R.error("未指定查询时间段!");
            }



            //如果只指定了UserID，则查询UserID
            if (qryDto.getUser_id() != 0)
            {
                if (qryDto.getTenant_openid().equals(""))    //租户不指定，则查询所有的
                {

                }
            }
            else if (!qryDto.getTenant_openid().equals(""))
            {

            }
            else
            {
                return R.error("账单查询条件错误!");
            }

            return R.error("账单查询失败");
        } catch (Exception ex) {
            return R.ok("账单查询异常:"+ex.getMessage());
        }
    }

    //微信用户读取图形文件
    @RequestMapping(value = "/getmapfile", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMapFile(MyUserMapFileDto userfileDto) {

        //检查传递参数是否正确
        if (null == userfileDto)
        {
            return R.error("文件名称错误!");
        }

        //通过用户的微信Openid查找用户的ID
        List<SysUserCount> lstUser = sysUserCountMapper.getUserByCount(userfileDto.getUser_count());
        if (null == lstUser || lstUser.size() == 0)
        {
            return R.error("业主微信认证配置错误!");
        }
        SysUserCount user = lstUser.get(0);
        if(user == null)
        {
            return R.error("业主信息错误!");
        }

        //检查文件是否存在
        InputStream in = null;
        String sUserID = String.format("%06d",user.getUser_id());
        String filePth= "D:\\mapfile\\"+sUserID+"\\"+userfileDto.getFile_name();
        System.out.print("读取图形文件:"+filePth+"\n");
        InputStreamReader isr = null;
        BufferedReader br = null;
        try
        {
            File file = new File(filePth);
            if(!file.exists()){

                // 文件不存在
                return R.error("文件不存在!");

            }


            StringBuffer sb = new StringBuffer();
            byte[] tempbytes = new byte[1024];
            int byteread = 0;
            in = new FileInputStream(file);
            String sRet = "";

            MyMapFileDto retDTO = new MyMapFileDto();
            retDTO.setFile_name(userfileDto.getFile_name());

            isr = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(isr);
            String line  = "";

            while ((line = br.readLine()) != null) {
                sRet += line;
            }

            // 读入多个字节到字节数组中，byteread为一次读入的字节数
          /*  while ((byteread = in.read(tempbytes)) != -1) {
                //  System.out.write(tempbytes, 0, byteread);
                String str = new String(tempbytes, 0, byteread);
                sb.append(str);
                sRet += str;
            }*/

            retDTO.setFile_content(sRet);

            //关闭读取的文件流
            in.close();
            isr.close();
            br.close();

            return R.ok(retDTO);
        }
        catch (Exception ex)
        {
            try
            {
                in.close();
                isr.close();
                br.close();
            }
            catch (Exception ex1)
            {

            }
            return R.error("读取文件异常!"+ex.getMessage());
        }
    }

}
