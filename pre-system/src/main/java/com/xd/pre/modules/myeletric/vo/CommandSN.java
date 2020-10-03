package com.xd.pre.modules.myeletric.vo;

import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import lombok.Data;

@Data
public class CommandSN {
    private int command_sn;
    private int command_state;

    public CommandSN(int sn)
    {
        command_sn = sn;
        command_state = IMyCommand.STATE_INIT;
    }


}
