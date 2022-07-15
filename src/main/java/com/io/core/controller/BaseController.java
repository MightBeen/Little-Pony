package com.io.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.io.core.common.utils.RedisUtil;
import com.io.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;


    public Page getPage(){
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 10);

        return new Page(current, size);
    }
}
