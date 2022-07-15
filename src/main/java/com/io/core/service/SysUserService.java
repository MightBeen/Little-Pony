package com.io.core.service;

import com.io.core.common.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);

    String getUserAuthInfo(long userId);

    void clearAuthInfoCacheByUser(String username);
    void clearAuthInfoCacheByRole(Long id);
    void clearAuthInfoCacheByMenu(Long id);
}
