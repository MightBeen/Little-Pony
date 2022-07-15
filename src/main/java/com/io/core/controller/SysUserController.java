package com.io.core.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.io.core.common.dto.PassDto;
import com.io.core.common.entity.SysRole;
import com.io.core.common.entity.SysUser;
import com.io.core.common.entity.SysUserRole;
import com.io.core.common.wrapper.ConstValue;
import com.io.core.common.wrapper.ResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    /**
     * 密码加密
     */
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    /**
     * 增删改查
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public ResultWrapper info(@PathVariable(name = "id") Long userid) {

        SysUser sysUser = sysUserService.getById(userid);

        Assert.notNull(sysUser, "该系统管理员不存在");

        List<SysRole> sysRoles = sysRoleService.listRolesByUserId(userid);

        sysUser.setSysRoles(sysRoles);

        return ResultWrapper.success(sysUser);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public ResultWrapper list(String userName) {
        Page<SysUser> data = sysUserService.page(getPage(),
                new QueryWrapper<SysUser>().like(StrUtil.isNotBlank(userName), "name", userName));

        data.getRecords().forEach(u -> {
            u.setSysRoles(sysRoleService.listRolesByUserId(u.getId()));
        });
        return ResultWrapper.success(data);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public ResultWrapper save(@Validated @RequestBody SysUser sysUser) {
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(ConstValue.STATUS_ON);

        // 设置默认密码和头像
        sysUser.setPassword(passwordEncoder.encode(ConstValue.DEFAULT_PSW));
        sysUser.setAvatar(ConstValue.DEFAULT_AVATAR);

        sysUserService.save(sysUser);

        return ResultWrapper.success(sysUser);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public ResultWrapper update(@Validated @RequestBody SysUser sysUser) {
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        sysUserService.updateById(sysUser);

        return ResultWrapper.success(sysUser.getUsername());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Transactional
    public ResultWrapper delete(@RequestBody Long[] userIds) {

        sysUserService.removeByIds(Arrays.asList(userIds));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", userIds));

        return ResultWrapper.success("");
    }

    /**
     * 分配权限api
     *
     * @param userId
     * @return
     */
    @Transactional
    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public ResultWrapper perm(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds) {

        List<SysUserRole> userRoles = new ArrayList<>();

        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);

            userRoles.add(sysUserRole);
        });

        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", userId));
        sysUserRoleService.saveBatch(userRoles);

        // 删除缓存
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearAuthInfoCacheByUser(sysUser.getUsername());

        return ResultWrapper.success("");
    }


    @PostMapping("/repass")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    public ResultWrapper repass(@RequestBody Long userId) {

        SysUser sysUser = sysUserService.getById(userId);

        sysUser.setPassword(passwordEncoder.encode(ConstValue.DEFAULT_PSW));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);

        return ResultWrapper.success("");
    }

    @PostMapping("/updatePass")
    public ResultWrapper updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {

        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if (!matches) {
            return ResultWrapper.fail("旧密码不正确");
        }

        sysUser.setPassword(passwordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return ResultWrapper.success("");
    }
}
