package com.xd.pre.modules.myeletric.device.command;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.pay.IPayment;


//退电命令
public class MyMeterDrawbackCommand extends MyMeterCommand {


    //支付方式:微信在线支付
    private int  pay_type = IPayment.PAY_TYPE_WX_PAY;

    //退款金额
    private float drawback_fee = 0.0f;

    //退电度数
    private float drawback_ep = 0.0f;

    public void SetDrawbackFee(float fDrawbackFee )
    {
        drawback_fee = fDrawbackFee;
    }

    public void SetDrawbackEP(float fEP)
    {
        drawback_ep = fEP;
    }


    public float getDrawbackEP(){
        return drawback_ep;
    }


    public MyMeterDrawbackCommand(MyCommandInfo commandInfo)
    {
        super(commandInfo);
    }


    public MyMeterDrawbackCommand(MyCommandInfo commandInfo, int nPayType, float drawbackEP,float fDrawbackFee)
    {
        super(commandInfo);

        //支付类型
        pay_type = nPayType;

        //退电电量
        drawback_ep  =drawbackEP;

        //退电金额
        drawback_fee = fDrawbackFee;

    }

    @Override
    public Integer getCommandType() {
        return COMMAND_MANNER_ADJUST;
    }

    /*****************************************************************************************************
     *                       state_init
     * 函数功能: 重载初始化状态机：设置电表设备
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    @Override
    protected void state_init()
    {

        if (null == command_info)
        {
            err_msg="电表命令信息为空,无法执行!";
            SwitchState(STATE_ABORT);
            return;
        }

        //关联电表设备
        IDevice device = ProductionContainer.getTheMeterDeviceContainer().getDevice(command_info.getDevice_name());
        if (null == device)
        {
            err_msg="电表设备未注册,无法执行!";
            SwitchState(STATE_ABORT);
            return;
        }
        MyMeter meter = MyDbStub.getInstance().getMeter(device.getDevNO());
        if(null == meter)
        {
            err_msg="电表设备未注册,无法执行!";
            SwitchState(STATE_ABORT);
            return;
        }

        //获取电表的剩余电度和电价
        MyMeterVo item = new MyMeterVo(meter);
        ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);
        String memo = String.format("退电操作,表号%06d,剩余电度%3.0f度,退电金额%3.0f，退电电度%3.0f",item.getMeter_id(),item.getLeft_ep(),drawback_ep,drawback_ep);

        //检查剩余电量是否比退电的电量大
        if (item.getLeft_ep() < drawback_ep)
        {
            err_msg="退电失败:退电电量比剩余电量大！";
            SwitchState(STATE_ABORT);
            return;
        }


        command_memo = memo;
        cmd_device = device;
        command_sn = command_info.getCommand_sn();
        start_tick = CommonFun.GetTick();
        expired_tick = start_tick+30;

        //保存命令信息到数据库中
        command_info.setCommand_memo(memo);
        MyDbStub.getInstance().saveDeviceCommandDB(command_info);


        SwitchState(STATE_PREPARE);

    }


}
