package com.io.portainer.service.ptr;

import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
public interface PtrUserEndpointService extends IService<PtrUserEndpoint> {
    /**
     * 从UE关系表中用节点获取用户
     */
    PtrUser getUserByEndPoint(PtrEndpoint endpoint);

}

