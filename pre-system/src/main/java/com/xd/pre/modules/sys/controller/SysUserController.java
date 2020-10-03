package com.xd.pre.modules.sys.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xd.MyWeixinStub;
import com.xd.pre.common.exception.PreBaseException;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.dto.MyWXUserFilterDto;
import com.xd.pre.modules.sys.domain.SysUserCount;
import com.xd.pre.modules.sys.dto.SysUserCountDTO;
import com.xd.pre.modules.sys.mapper.SysUserCountMapper;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import com.xd.pre.common.constant.PreConstant;
import com.xd.pre.modules.sys.domain.SysUser;
import com.xd.pre.modules.sys.dto.UserDTO;
import com.xd.pre.modules.sys.service.ISysUserService;
import com.xd.pre.modules.sys.util.EmailUtil;
import com.xd.pre.common.utils.R;
import com.xd.pre.modules.sys.util.PreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.weixin4j.model.sns.SnsUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lihaodong
 * @since 2019-04-21
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserCountMapper sysUserCountMapper;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 保存用户包括角色和部门
     *
     * @param userDto
     * @return
     */
    @SysOperaLog(descrption = "保存用户包括角色和部门")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:add')")
    public R insert(@RequestBody UserDTO userDto) {
        return R.ok(userService.insertUser(userDto));
    }


    /**
     * 获取用户列表集合
     *
     * @param page
     * @param userDTO
     * @return
     */
    @SysOperaLog(descrption = "查询用户集合")
    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:view')")
    public R getList(Page page, UserDTO userDTO) {
        return R.ok(userService.getUsersWithRolePage(page, userDTO));
    }

    /**
     * 更新用户包括角色和部门
     *
     * @param userDto
     * @return
     */
    @SysOperaLog(descrption = "更新用户包括角色和部门")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:user:update')")
    public R update(@RequestBody UserDTO userDto) {
        return R.ok(userService.updateUser(userDto));
    }

    /**
     * 删除用户包括角色和部门
     *
     * @param userId
     * @return
     */
    @SysOperaLog(descrption = "根据用户id删除用户包括角色和部门")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public R delete(@PathVariable("userId") Integer userId) {
        return R.ok(userService.removeUser(userId));
    }


    //通过uuid获取微信认证用户的信息
    @SysOperaLog(descrption = "获取微信用户信息")
    @RequestMapping(value = "/getwxuserinfobyuuid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfobyUUID( MyWXUserFilterDto filter) {

        //获取code
        String uuid = filter.getParam();

        //获取当前连接的用户ID
        try {

            //将用户信息记录到Redis缓冲中
            String userinfo = redisTemplate.opsForValue().get(uuid);
            return R.ok(userinfo);

        } catch (Exception ex) {

            return R.error("获取微信用户信息失败");
        }
    }

    /**
     * 重置密码
     *
     * @param userId
     * @return
     */
    @SysOperaLog(descrption = "重置密码")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:user:rest')")
    public R restPass(@PathVariable("userId") Integer userId) {
        return R.ok(userService.restPass(userId));
    }


    /**
     * 获取个人信息
     *
     * @return
     */
    @SysOperaLog(descrption = "获取个人信息")
    @GetMapping("/info")
    public R getUserInfo() {
        return R.ok(userService.findByUserInfoName(SecurityUtil.getUser().getUsername()));
    }


    /**
     * 修改密码
     *
     * @return
     */
    @SysOperaLog(descrption = "修改密码")
    @PutMapping("updatePass")
    @PreAuthorize("hasAuthority('sys:user:updatePass')")
    public R updatePass(@RequestParam String oldPass, @RequestParam String newPass) {
        // 校验密码流程
        SysUser sysUser = userService.findSecurityUserByUser(new SysUser().setUsername(SecurityUtil.getUser().getUsername()));
        if (!PreUtil.validatePass(oldPass, sysUser.getPassword())) {
            throw new PreBaseException("原密码错误");
        }
        if (StrUtil.equals(oldPass, newPass)) {
            throw new PreBaseException("新密码不能与旧密码相同");
        }
        // 修改密码流程
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setPassword(PreUtil.encode(newPass));
        return R.ok(userService.updateUserInfo(user));
    }

    /**
     * 检测用户名是否存在 避免重复
     *
     * @param userName
     * @return
     */
    @PostMapping("/vailUserName")
    public R vailUserName(@RequestParam String userName) {
        SysUser sysUser = userService.findSecurityUserByUser(new SysUser().setUsername(userName));
        return R.ok(ObjectUtil.isNull(sysUser));
    }

    /**
     * 发送邮箱验证码
     *
     * @param to
     * @param request
     * @return
     */
    @PostMapping("/sendMailCode")
    public R sendMailCode(@RequestParam String to, HttpServletRequest request) {
        emailUtil.sendSimpleMail(to, request);
        return R.ok();
    }

    /**
     * 修改密码
     *
     * @return
     */
    @SysOperaLog(descrption = "修改邮箱")
    @PutMapping("updateEmail")
    @PreAuthorize("hasAuthority('sys:user:updateEmail')")
    public R updateEmail(@RequestParam String mail, @RequestParam String code, @RequestParam String pass, HttpServletRequest request) {
        // 校验验证码流程
        String ccode = (String) request.getSession().getAttribute(PreConstant.RESET_MAIL);
        if (ObjectUtil.isNull(ccode)) {
            throw new PreBaseException("验证码已过期");
        }
        if (!StrUtil.equals(code.toLowerCase(), ccode)) {
            throw new PreBaseException("验证码错误");
        }
        // 校验密码流程
        SysUser sysUser = userService.findSecurityUserByUser(new SysUser().setUsername(SecurityUtil.getUser().getUsername()));
        if (!PreUtil.validatePass(pass, sysUser.getPassword())) {
            throw new PreBaseException("密码错误");
        }
        // 修改邮箱流程
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setEmail(mail);
        return R.ok(userService.updateUserInfo(user));
    }


    //获取用户的账户信息
    @SysOperaLog(descrption = "获取用户的结算账户信息")
    @RequestMapping(value = "/getpromotioncount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R GetPromotionCount() {

        PreSecurityUser user = SecurityUtil.getUser();
        if (null == user)
        {
            return R.error("更新失败,安全认证失败!");
        }


        //获取当前连接的用户ID
        try {

            List<SysUserCount> lstCount = sysUserCountMapper.getUserCountByUserID(user.getUserId());
            SysUserCount userCount = lstCount.get(0);
            if (null == userCount)
            {
                return R.error("获取用户结算账号为空");
            }

            return R.ok(userCount);

        } catch (Exception ex) {

            return R.error("获取用户结算账号失败"+ex.getMessage()+"\n");
        }
    }

    //更新业主的结算账号
    @SysOperaLog(descrption = "更新业主的结算账号")
    @RequestMapping(value = "/updatepromotioncount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R updatePromotionCount( SysUserCountDTO userCountDTO) {

        if(null == userCountDTO)
        {
            return R.error("更新失败,业主账号为空");
        }


        //获取当前连接的用户ID
        try {

            SysUserCountDTO userUpt = new SysUserCountDTO();
            PreSecurityUser user = SecurityUtil.getUser();
            if (null == user)
            {
                return R.error("更新失败,安全认证失败!");
            }

            userUpt.setUser_id(user.getUserId());
            userUpt.setCount_type(userCountDTO.getCount_type());
            userUpt.setUser_count(userCountDTO.getUser_count());

            int ret = sysUserCountMapper.updateUserCount(userUpt);
            if (ret == 1)
            {
                return R.ok("更新结算账户成功");
            }
            else
            {
                return R.ok("更新结算账户失败");
            }


        } catch (Exception ex) {

            return R.error("获取用户结算账号失败");
        }
    }

}

