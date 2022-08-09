package com.io.portainer.service.ptr;

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
     * 从申请中修改用户,目前专门为请求延长expired服务
     */
    public PtrUserEndpoint updatePtrUserEndpointData(PtrUserEndpoint u);
}

