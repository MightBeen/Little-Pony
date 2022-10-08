package com.io.portainer.mapper.ptr;

import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Mapper
public interface PtrUserEndpointMapper extends BaseMapper<PtrUserEndpoint> {
    PtrUser getUserByEndPoint(PtrEndpoint endpoint);
}
