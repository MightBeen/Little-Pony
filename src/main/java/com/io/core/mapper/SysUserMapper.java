package com.io.core.mapper;

import com.io.core.common.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<Long> getNavMenuIds(long userId);

    List<SysUser> listByMenuId(Long id);

}
