package com.io.portainer.service.ptr;

import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 即gpu资源虚拟机 服务类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
public interface PtrEndpointService extends IService<PtrEndpoint> {

    // TODO: 添加PtrEndPointService分页查询
    // TODO: 添加通过Portainer查询
    List<PtrEndpoint> getPtrEndpoints();

    PtrEndpoint getPtrEndpointById(Long id);

    List<Long> getUserIdsByEndPoint(PtrEndpoint endpoint);

    List<PtrEndpoint> updatePtrEndpointsDataFromPtr();
}
