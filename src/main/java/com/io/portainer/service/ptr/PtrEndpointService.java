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




    List<PtrEndpoint> getPtrEndpoints();
    List<PtrEndpoint> selectPtrEndpointsByPage(Integer pageNo, Integer pageSize);

    PtrEndpoint getPtrEndpointById(Long id);

    List<Long> getUserIdsByEndPoint(PtrEndpoint endpoint);

    List<PtrEndpoint> updatePtrEndpointsDataFromPtr();
}
