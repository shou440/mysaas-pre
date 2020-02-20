package com.xd.pre.modules.myeletric.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xd.pre.common.exception.PreBaseException;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyTalentInfo;
import com.xd.pre.modules.myeletric.dto.MyNewRoomParam;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.dto.MyRoomFilterDto;
import com.xd.pre.modules.myeletric.dto.MyTalentInfoDto;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.modules.myeletric.service.IMyTalentInfoService;
import com.xd.pre.modules.myeletric.vo.MyTalentInfoVo;
import com.xd.pre.modules.sys.dto.RoleDTO;
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

    @Autowired
    private IMyTalentInfoService myTalentInfoService;


    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取业主房间信息")
    @RequestMapping(value = "/gettalentrooms", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getRoomList(MyRoomFilterDto  request) {

        Integer roomid = 0;

        return R.ok(myRoomService.getRoomInfo(request.getAreaid()));
    }


    @PreAuthorize("hasAuthority('sys:room:edit')")
    @SysOperaLog(descrption = "创建新房间")
    @RequestMapping(value = "/createroom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R CreateNewRoom(MyNewRoomParam roominfo) {

        //提取当前最大的房间编号
        MyRoomDto room = new MyRoomDto();
        room.setRoom_name(roominfo.getRoom_name());
        room.setArea_id(roominfo.getArea_id());

        //创建新房间
        try
        {
            room = myRoomService.createNewRoom(room);
            return R.ok(room);
        }
        catch (Exception ex)
        {
            return R.error("添加房间异常,请检查同一园区是否有重复的名称！");
        }

    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "修改房间信息")
    @RequestMapping(value = "/updateroominfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R updateRoomInfo(MyRoomDto room) {

        //修改房间
        try
        {
            myRoomService.updateRoominfo(room);
            return R.ok("修改成功");
        }
        catch (Exception ex)
        {
            return R.error("修改异常");
        }

    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取租户信息")
    @RequestMapping(value = "/gettanlentinfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getTalentInfo(MyTalentInfoDto talentInfo) {

        //修改房间
        try
        {
           MyTalentInfo talentInfoVo =  myTalentInfoService.getTanlentInfo(talentInfo.getRoom_id());
           if (null == talentInfoVo)
           {
               return R.ok("获取租户信息为空");
           }

           return R.ok(talentInfoVo);
        }
        catch (Exception ex)
        {
            return R.error("获取租户信息异常");
        }

    }
}
