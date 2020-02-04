package com.xd.pre.modules.myeletric.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xd.pre.common.exception.PreBaseException;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.security.util.SecurityUtil;
import com.xd.pre.common.constant.PreConstant;
import com.xd.pre.modules.sys.util.EmailUtil;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.sys.util.PreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/room")
public class MyRoomController {

    @Autowired
    private IMyRoomService myRoomService;


    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取业主房间信息")
    @GetMapping
    public R getRoomList() {



        return R.ok(myRoomService.getRoomInfo());
    }
}
