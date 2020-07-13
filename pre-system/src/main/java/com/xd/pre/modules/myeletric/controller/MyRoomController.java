package com.xd.pre.modules.myeletric.controller;


import com.xd.MyWeixinStub;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyRoomTenant;
import com.xd.pre.modules.myeletric.dto.*;
import com.xd.pre.modules.myeletric.mapper.MyRoomTenantMapper;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.weixin4j.model.user.User;

import java.util.List;


@RestController
@RequestMapping("/room")
public class MyRoomController {

    @Autowired
    private IMyRoomService myRoomService;

    @Autowired
    private MyRoomTenantMapper myRoomTenantMapper;

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取业主房间信息")
    @RequestMapping(value = "/gettenantrooms", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
            return R.error("添加房间异常,请检查同一园区是否有重复的名称！"+ex.getMessage());
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
    @SysOperaLog(descrption = "修改房间租户信息")
    @RequestMapping(value = "/updateroomtenant", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R updateRoomTenant(MyRoomDto room) {

        //修改房间
        try
        {
            myRoomService.updateRoomtenant(room);
            return R.ok("修改成功");
        }
        catch (Exception ex)
        {
            return R.error("修改异常");
        }

    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "出租房间")
    @RequestMapping(value = "/lessorroom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R LessorRoom(MyRoomLessorDto roomLessorDto) {

        //修改房间
        try
        {
            //检查房间是否存在
            if (null == roomLessorDto)
            {
                return R.error("房间参数错误");
            }

            MyRoom room = myRoomService.getRoomByID(roomLessorDto.getRoom_id());
            if (null == room)
            {
                return R.error("房间不存在！");
            }

            if (room.getRoom_status() != 0)
            {
                return R.error("房间已经出租！");
            }

            if (room.getRoom_name() == null || room.getRoom_name() == "")
            {
                return R.error("房间编号不能为空！");
            }

            if (room.getTenant_name() == null || room.getTenant_name() == "")
            {
                return R.error("租户名称不能为空！");
            }

           if(0 ==  myRoomService.LessorRoom(roomLessorDto))
           {
               return R.error("出租房间失败");
           }

            return R.ok("修改成功");
        }
        catch (Exception ex)
        {
            return R.error("修改异常");
        }

    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "退租房间")
    @RequestMapping(value = "/unlessorroom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R UnLessorRoom(MyRoomLessorDto roomLessorDto) {

        //退租房间
        try
        {
            //检查房间是否存在
            if (null == roomLessorDto)
            {
                return R.error("房间参数错误");
            }

            MyRoom room = myRoomService.getRoomByID(roomLessorDto.getRoom_id());
            if (null == room)
            {
                return R.error("房间不存在！");
            }

            if (room.getRoom_status() == 0)
            {
                return R.error("房间已经空置状态，无法退租！");
            }

            //办理房间退租
            String sErr = "";
            int ret = myRoomService.UnLessorRoom(roomLessorDto,sErr);
            if (ret == 0)
            {
                return R.error("退租异常:"+sErr);
            }


            return R.ok("退租成功");
        }
        catch (Exception ex)
        {
            return R.error("退租异常"+ex.getMessage());
        }

    }

    //通过openid拉取用户的信息,在此调用此函数是为了避免在MyWeixinController中权限
    // 认证没有传递X-Token导致的强制退出当前登录问题
    @SysOperaLog(descrption = "获取租户微信信息")
    @PreAuthorize("hasAuthority('sys:room:view')")
    @RequestMapping(value = "/getwxuserdetail", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getwxUserDetailByOpenid( MyWXUserFilterDto filter) {

        //获取code
        String openid = filter.getOpenid();

        //获取当前连接的用户ID
        try {
            System.out.print("Query Wechat Info"+openid);
            User user = MyWeixinStub.getTheMyWeixinStub().GetUserDetailByOpenid(openid);
            System.out.print(user);
            return R.ok(user);
        } catch (Exception ex) {

            return R.error("获取微信用户信息失败");
        }
    }
}
