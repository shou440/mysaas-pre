package com.xd.pre.modules.myeletric.controller;


import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.service.IMyAreaService;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/area")
public class MyAreaController {

    @Autowired
    private IMyAreaService iMyAreaService;



    //查看业主的所有园区
    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取业主园区信息")
    @GetMapping("/gettenantareas")
    public R getAreaList( ) {

        String ownerOpenid = "";
        //获取当前连接的用户ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PreSecurityUser user = SecurityUtil.getUser();
        List<MyArea> list=new ArrayList<MyArea>();
        if (null != user)
        {
            list = iMyAreaService.getAreaByOwnerOpenid(user.getUserId());
        }
        return R.ok(list);
    }

    //注册新的园区


}
