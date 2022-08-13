package com.io.portainer.service.ptr.impl;

import cn.hutool.http.HttpException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.connect.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.mapper.ptr.PtrEndpointMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 即gpu资源虚拟机 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Service
public class PtrEndpointServiceImpl extends ServiceImpl<PtrEndpointMapper, PtrEndpoint> implements PtrEndpointService {

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    @Autowired
    PortainerConnector portainerConnector;

    private String baseUrl = "/endpoints";

    /**
     * 从数据库中获取endpoint，并生成用户列表
     * @return
     */
    @Override
    public List<PtrEndpoint> getPtrEndpoints() {
        List<PtrEndpoint> endPoints = this.list();
        endPoints.forEach(edp -> {
            edp.setUserIds(getUserIdsByEndPoint(edp));
        });
        return endPoints;
    }

    @Override
    public PtrEndpoint getPtrEndpointById(Long id) {
        PtrEndpoint endpoint = this.getById(id);
        if (endpoint == null) {
            return null;
        }
        endpoint.setUserIds(getUserIdsByEndPoint(endpoint));
        return endpoint;
    }

    @Override
    public List<Long> getUserIdsByEndPoint(PtrEndpoint endpoint){
        List<Long> ids = new ArrayList<>();
        List<PtrUserEndpoint> list = ptrUserEndpointService.list(new QueryWrapper<PtrUserEndpoint>().eq("endPoint_id", endpoint.getId()));
        list.forEach(i -> {
            ids.add(i.getUserId());
        });
        return ids;
    }

    @Override
    @Transactional
    public List<PtrEndpoint> updatePtrEndpointsDataFromPtr() {
        // 从portainer获取的数据
        List<PtrEndpoint> ptrEndpointList = null;

        // 从管理系统数据库中获取的数据
        List<PtrEndpoint> dbEndpoints = this.list();

        // 用于返回
        List<PtrEndpoint> newEndpoints = null;

        PtrJsonParser<PtrEndpoint> parser = new PtrJsonParser<>(PtrEndpoint.class);

        List<Long> ids = new ArrayList<>();

        HashMap<Long, PtrEndpoint> edMap = new HashMap<>();


        Response ptrResponse;
        try {
            ptrResponse = portainerConnector.getRequest(baseUrl);
            if (ptrResponse.code() == 200) {
                ptrEndpointList = parser.parseJsonArray(ptrResponse.body().string());
            } else {
                throw new HttpException("Portainer 连接异常: " + ptrResponse.code());
            }

            dbEndpoints.forEach(u -> {
                ids.add(u.getId());
                edMap.put(u.getId(), u);
            });


            // 将数据合并
            for (PtrEndpoint endpoint : ptrEndpointList) {
                PtrEndpoint u = edMap.get(endpoint.getId());
                if (u != null) {
                    CommonUtils.fieldInjection(parser.getUpdatableFields(), endpoint, u);
                    endpoint.setUpdated(LocalDateTime.now());
                } else
                    endpoint.setCreated(LocalDateTime.now());
            }

            // 同时更新ptr_user_endpoint表
            // 更新 user_endpoint 表

            // ue表所有数据
            List<PtrUserEndpoint> preUeLists = ptrUserEndpointService.list();

            List<PtrUserEndpoint> newUeLists = CommonUtils.flatEndpoints(ptrEndpointList);

            newUeLists.forEach(newItem -> {
                newItem.setCreated(LocalDateTime.now());
                newItem.setExpired(LocalDateTime.now().plusDays(31));
                preUeLists.forEach(preItem -> {
                    if (Objects.equals(preItem.getEndpointId(), newItem.getEndpointId()) && Objects.equals(preItem.getUserId(), newItem.getUserId())) {
                        newItem.setId(preItem.getId());
                        newItem.setCreated(preItem.getCreated());
                        newItem.setExpired(preItem.getExpired());
                        newItem.setUpdated(LocalDateTime.now());
                    }
                });
            });

            ptrUserEndpointService.removeByIds(CommonUtils.entityToIdList(preUeLists));
            ptrUserEndpointService.saveBatch(newUeLists);

            // =========== 更endpoints表 ============
            newEndpoints = ptrEndpointList;

            // 移除旧数据
            this.removeByIds(ids);
            this.saveBatch(newEndpoints);

        } catch (IOException | HttpException e) {
            log.error("Error getting endpoints from portainer :" + e.getMessage());
            return null;
        }

        return newEndpoints;
    }
}
