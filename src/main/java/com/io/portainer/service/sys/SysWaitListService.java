package com.io.portainer.service.sys;

import com.io.portainer.data.entity.sys.SysWaitList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.io.portainer.data.vo.WaitListVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author me
 * @since 2022-07-15
 */
public interface SysWaitListService extends IService<SysWaitList> {

    List<WaitListVo> getWaitLists(Integer pageNumber, Integer amount);
}

