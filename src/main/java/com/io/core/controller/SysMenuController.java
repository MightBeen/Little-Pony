package com.io.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.dto.SysMenuDto;
import com.io.core.common.entity.SysMenu;
import com.io.core.common.entity.SysRoleMenu;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.core.common.entity.SysUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    /**
     * 返回用户权限范围内能使用的菜单
     * @param principal spring-security 的登录用户
     */
    @GetMapping("/nav")
    public ResultWrapper nav(Principal principal) {

        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        // 获取权限信息

        String userAuthInfo = sysUserService.getUserAuthInfo(sysUser.getId());

        String[] authInfos = StringUtils.tokenizeToStringArray(userAuthInfo, ",");

        // 获取导航栏信息

        List<SysMenuDto> navs = sysMenuService.getCurrentUserNav();

        HashMap<String, Object> data = new HashMap<>();
        data.put("authorities", authInfos);
        data.put("nav", navs);
        return ResultWrapper.success(data);
    }

    /**
     *  增删改查
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public ResultWrapper info(@PathVariable(name = "id") Long id) {
        return ResultWrapper.success(sysMenuService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public ResultWrapper list(Long id) {
        return ResultWrapper.success(sysMenuService.tree());
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public ResultWrapper save(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.save(sysMenu);

        sysUserService.clearAuthInfoCacheByMenu(sysMenu.getId());
        return ResultWrapper.success(sysMenu);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public ResultWrapper update(@Validated @RequestBody SysMenu sysMenu) {

        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);

        sysUserService.clearAuthInfoCacheByMenu(sysMenu.getId());
        return ResultWrapper.success(sysMenu);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public ResultWrapper delete(@PathVariable("id") Long id) {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return ResultWrapper.fail("请先删除子菜单");
        }

        sysMenuService.removeById(id);
        // 同步删除中间关联表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));

        // 清除相关权限缓存
        sysUserService.clearAuthInfoCacheByMenu(id);

        return ResultWrapper.success("");
    }
}
