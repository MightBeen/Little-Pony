package com.io.portainer.mapper.sys;

import com.io.portainer.data.entity.sys.SysCheckList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作 Mapper 接口
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
public interface SysCheckListMapper extends BaseMapper<SysCheckList> {
    List<LocalDateTime> getExpiredDatesByType(Integer resourceType);
}
