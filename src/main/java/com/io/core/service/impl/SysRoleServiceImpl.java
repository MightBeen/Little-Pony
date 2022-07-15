package com.io.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.entity.SysRole;
import com.io.core.mapper.SysRoleMapper;
import com.io.core.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Override
    public List<SysRole> listRolesByUserId(Long userid) {
        String sql = "SELECT role_id from sys_user_role where user_id = " + userid;
        return this.list(new QueryWrapper<SysRole>().inSql("id", sql));
    }
}
