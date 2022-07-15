package com.io.core.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.io.core.common.entity.SysRole;
import com.io.core.common.entity.SysRoleMenu;
import com.io.core.common.entity.SysUserRole;
import com.io.core.common.wrapper.ConstValue;
import com.io.core.common.wrapper.ResultWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {


    /**
     * 增删改查
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public ResultWrapper info(@PathVariable(name = "id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        
        // 获取角色相关联的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        sysRole.setMenuIds(menuIds);
        return ResultWrapper.success(sysRole);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public ResultWrapper list(String name) {
        Page<SysRole> data = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>().like(StrUtil.isNotBlank(name), "name", name));

        return ResultWrapper.success(data);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public ResultWrapper save(@Validated @RequestBody SysRole sysRole) {
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(ConstValue.STATUS_ON);

        sysRoleService.save(sysRole);
        return ResultWrapper.success(sysRole);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public ResultWrapper update(@Validated @RequestBody SysRole sysRole) {
        sysRole.setUpdated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);

        // 更新缓存
        sysUserService.clearAuthInfoCacheByRole(sysRole.getId());
        return ResultWrapper.success(sysRole);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Transactional
    public ResultWrapper delete(@RequestBody Long[] roleIds) {

        sysRoleService.removeByIds(Arrays.asList(roleIds));

        // 同步删除中间关联表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", roleIds));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", roleIds));

        // 清除相关权限缓存
        Arrays.stream(roleIds).forEach(id -> {
            sysUserService.clearAuthInfoCacheByRole(id);
        });

        return ResultWrapper.success(null);
    }

    /**
     * 分配权限api
     * @param roleId
     * @param menuIds
     * @return
     */
    @Transactional
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public ResultWrapper perm(@PathVariable("roleId") Long roleId, @RequestBody Long[] menuIds) {

        List<SysRoleMenu> roleMenuList = new ArrayList<>();

        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);

            roleMenuList.add(roleMenu);
        });

        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(roleMenuList);

        return ResultWrapper.success("");
    }

}
