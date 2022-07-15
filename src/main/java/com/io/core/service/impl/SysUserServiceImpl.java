package com.io.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.utils.RedisUtil;
import com.io.core.common.entity.SysMenu;
import com.io.core.common.entity.SysRole;
import com.io.core.common.entity.SysUser;
import com.io.core.mapper.SysUserMapper;
import com.io.core.service.SysMenuService;
import com.io.core.service.SysRoleService;
import com.io.core.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 用户认证信息缓存时间(秒)
     */
    private final int duration = 3600;
    String key = "GrantedAuthority";



    @Autowired
    SysRoleService roleService;

    @Autowired
    SysMenuService menuService;

    @Autowired
    SysUserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;


    /**
     *  通过用户名称获取数据库内的用户对象
     * @param username sys的用户名称
     * @return 用户对象
     */
    @Override
    public SysUser getByUsername(String username){
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }


    /**
     * 通过用户名称删除缓存
     * @param username 用户名称
     */
    @Override
    public void clearAuthInfoCacheByUser(String username) {
        redisUtil.del(key + username);
    }

    @Override
    public void clearAuthInfoCacheByRole(Long id) {
        List<SysUser> users = this.list(new QueryWrapper<SysUser>().inSql("id",
                "SELECT user_id FROM sys_user_role WHERE role_id = " + id));
        users.forEach(u ->{
            this.clearAuthInfoCacheByUser(u.getUsername());
        });
    }

    @Override
    public void clearAuthInfoCacheByMenu(Long id) {
        List<SysUser> users = userMapper.listByMenuId(id);

        for (SysUser u : users) {
            this.clearAuthInfoCacheByUser(u.getUsername());
        }
    }

    /**
     * 通过用户id 获取用户认证信息。
     * 结果用 “，”分隔。
     * 内部未对id不存在情况进行处理，使用前须保证id可用
     * @param userId
     * @return
     */
    @Override
    public String getUserAuthInfo(long userId) {
        SysUser sysUser = userMapper.selectById(userId);

        // 空指针处理
        if (sysUser == null) {
            log.error("-方法调用错误，用户id不存在-");
            return "";
        }

        // 如果缓存中已有用户信息
        if (redisUtil.hasKey(key + sysUser.getUsername())) {
            return (String) redisUtil.get(key + sysUser.getUsername());
        }


        String authInfo = "";

        // 获取角色编码
        List<SysRole> roles = roleService.list(new QueryWrapper<SysRole>().inSql("id",
                "SELECT role_id from sys_user_role where user_id = " + sysUser.getId()));

        if (roles.size() > 0) {
            authInfo = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
        }

        // 获取菜单操作编码
        List<Long> menuIds = userMapper.getNavMenuIds(sysUser.getId());

        if (menuIds.size() > 0) {
            String menuPerms = menuService.listByIds(menuIds).stream().map(SysMenu::getPerms).collect(Collectors.joining(","));

            if (!authInfo.isEmpty())
                authInfo = authInfo.concat(",").concat(menuPerms);
            else
                authInfo = authInfo.concat(menuPerms);
        }

        // 存入缓存
        redisUtil.set(key + sysUser.getUsername(), authInfo, duration);

        return authInfo;
    }

}
