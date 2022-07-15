package com.io.core.service;

import com.io.core.common.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listRolesByUserId(Long userUd);
}
