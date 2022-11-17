package com.io.portainer.mapper.sys;

import com.io.portainer.data.entity.sys.SysWaitList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.io.portainer.data.vo.WaitListVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author me
 * @since 2022-07-15
 */
public interface SysWaitListMapper extends BaseMapper<SysWaitList> {
    List<WaitListVo> getWaitLists(Integer start, Integer amount);

    List<WaitListVo> listWaitLists();
}
