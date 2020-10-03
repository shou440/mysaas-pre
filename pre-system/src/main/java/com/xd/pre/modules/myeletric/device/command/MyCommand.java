package com.xd.pre.modules.myeletric.device.command;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.buffer.MySystemRedisBuffer;
import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductFunction;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;

import java.util.concurrent.LinkedBlockingQueue;


//用户命令基础类:清除累计电度、清除剩余电度、调整剩余电度、人工强制拉闸、人工强制合闸等
public class MyCommand implements  IMyCommand{

    protected Integer start_tick = 0;
    protected Integer command_sn = 0;
    protected Integer expired_tick = 0;
    protected Integer cmd_state = IMyCommand.STATE_INIT;
    protected IDevice cmd_device = null;
    protected Integer last_tick = 0;                          //上一次发送命令的时刻
    protected String  err_msg = "";
    protected String  command_memo = "";
    protected MyCommandInfo command_info = null;

    //用户新的命令同步消息队列
    private LinkedBlockingQueue<MyCommandMessage> queue_command_msg = new LinkedBlockingQueue<MyCommandMessage>();

    public MyCommand(MyCommandInfo commandInfo)
    {
        start_tick = commandInfo.getStart_tick();
        expired_tick = commandInfo.getExpire_tick();
        command_info = commandInfo;
    }

    protected void SwitchState(int nNextState)
    {
        cmd_state = nNextState;
        last_tick = CommonFun.GetTick();

        //更新内存数据库
        if (nNextState != STATE_DISPOSE)
        {
            freshRedis();
        }

        //保存命令执行结果
        if (nNextState == STATE_CMPLT || nNextState == STATE_ABORT)
        {
            UptCommandStatetoDB(nNextState,err_msg);     //保存命令执行状态
        }
    }

    //是否超时
    @Override
    public boolean isExpired()
    {
        if (expired_tick < CommonFun.GetTick())
        {
            return true;
        }

        return false;
    }

    @Override
    public Integer getCommandType() {
        return COMMAND_INVALID;
    }

    @Override
    public Integer getStartTick() {
        return start_tick;
    }

    @Override
    public Integer getCommandSN() {
        return command_sn;
    }

    @Override
    public Integer ExipredTick() {
        return expired_tick;
    }

    @Override
    public Integer getState() {
        return cmd_state;
    }

    @Override
    public String getErrMsg() {
        return err_msg;
    }

    @Override
    public String getFunctionName() {
        return null;
    }

    @Override
    public IDevice getDevice() {
        return cmd_device;
    }

    @Override
    public IProductFunction getFunction() {
        return null;
    }

    @Override
    public MyCommandInfo getCommandInfo() {
        return command_info;
    }


    //更新电表状态到数据库中
    private boolean UptCommandStatetoDB(int nRetsult,String err_msg)
    {
        command_info.setResult_code(nRetsult);
        command_info.setErr_msg(err_msg);
        if (MyDbStub.getInstance().uptDeviceCommand(command_info) == 1)
        {
            return true;
        }
        return false;
    }

    /*****************************************************************************************************
     *                       state_init
     * 函数功能: 状态机(初始化状态)
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    protected void state_init()
    {
        //保存执行命令到数据库中
        MyDbStub.getInstance().saveDeviceCommandDB(command_info);


        SwitchState(STATE_PREPARE);

    }

    /*****************************************************************************************************
     *                       state_prepare
     * 函数功能: 状态机(准备状态)
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    protected void state_prepare()
    {

        //将命令添加到设备中
        if(null != cmd_device)
        {
            cmd_device.onCommand(this);
            SwitchState(STATE_COMMITING);
            return;
        }

        SwitchState(STATE_ABORT);
    }

    /*****************************************************************************************************
     *                       state_commit
     * 函数功能: 状态机(执行中)
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    protected void state_commit()
    {
        //判断是否超时
        if (isExpired())
        {
            err_msg = "命令执行超时";     //设置错误代码为执行超时
            SwitchState(STATE_ABORT);
            return;
        }

        //提取执行消息，检查是否执行成功
        MyCommandMessage msg  = fetchMessage();
        if (null != msg)
        {
            switch (msg.getMessage())
            {
                case MyCommandMessage.CMD_MSG_CMPLT:
                {
                    SwitchState(STATE_CMPLT);
                    break;
                }
                case MyCommandMessage.CMD_MSG_ABORT:
                {
                    err_msg = msg.getErrMsg();
                    SwitchState(STATE_ABORT);
                    break;
                }
            }
        }
    }
    /*****************************************************************************************************
     *                       state_cmplt
     * 函数功能: 状态机(完成状态)
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    protected void state_cmplt()
    {

        SwitchState(STATE_DISPOSE);
    }
    /*****************************************************************************************************
     *                       state_abort
     * 函数功能: 状态机(异常状态)
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    protected void state_abort()
    {
        SwitchState(STATE_DISPOSE);
    }
    /*****************************************************************************************************
     *                       CallTick
     * 函数功能: 命令状态机回调函数
     * 输入参数: 无
     * 返回值  : 无
     * 日   期 : 2020-8-31
     * 作   者 : zxr
     *
     ******************************************************************************************************/
    @Override
    public void CallTick() {

        switch (cmd_state)
        {
            case STATE_INIT:
            {
                state_init();
                break;
            }
            case STATE_PREPARE:
            {
                state_prepare();
                break;
            }
            case STATE_COMMITING:
            {
                state_commit();
                break;
            }
            case STATE_CMPLT:
            {
                state_cmplt();
                break;
            }
            case STATE_ABORT:
            {
                state_abort();
                break;
            }
            case STATE_DISPOSE:
            {
                break;
            }

        }
    }

    @Override
    public boolean IsNeedDispose() {
        return false;
    }

    @Override
    public void onMessage(MyCommandMessage msg) {

        try{
            queue_command_msg.put(msg);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public MyCommandMessage fetchMessage() {

        if (null == queue_command_msg || queue_command_msg.size() == 0)
        {
            return null;
        }

        try
        {
            MyCommandMessage msg  = queue_command_msg.poll();
            return msg;
        }
        catch (Exception ex){
            return null;
        }

    }

    //刷新命令状态到Redis
    @Override
    public void freshRedis() {

        String sState = String.format("%d",getState());
        String key = "commandstate_"+String.format("%012d",getCommandSN());
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(key,sState);

        //设置错误描述，主要是给前端报告错误用
        String errkey = "command-errmsg_"+String.format("%012d",getCommandSN());
        MySystemRedisBuffer.getTheSinTon().SaveBufferItem(errkey,err_msg);
    }
}
