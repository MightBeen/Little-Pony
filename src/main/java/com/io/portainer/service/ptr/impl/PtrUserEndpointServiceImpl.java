package com.io.portainer.service.ptr.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.portainer.common.timer.Checkable;
import com.io.portainer.common.timer.RegularService;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.mapper.ptr.PtrUserEndpointMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.portainer.service.ptr.PtrUserService;
import com.io.portainer.service.sys.SysLogService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Service
@Slf4j
public class PtrUserEndpointServiceImpl extends ServiceImpl<PtrUserEndpointMapper, PtrUserEndpoint>
        implements PtrUserEndpointService, RegularService<PtrUserEndpoint> {
    @Autowired
    @Lazy
    PtrEndpointService ptrEndpointService;

    @Autowired
    @Lazy
    PtrUserService ptrUserService;

    @Autowired
    PortainerConnector portainerConnector;

    @Autowired
    SysLogService sysLogService;
    /**
     * 顺便会更新节点
     */
    @Override
    @Transactional
    public PriorityQueue<Checkable> updateAll() {
        // 先执行节点的更新
        List<PtrEndpoint> endpoints = ptrEndpointService.updatePtrEndpointsDataFromPtr();

        // 处理僵尸用户
        endpoints.forEach(e -> {
            Iterator<Long> it = e.getUserIds().iterator();
            boolean updated = false;
            while (it.hasNext()) {
                Long userid = it.next();
                PtrUser user = ptrUserService.getById(userid);
                if (user == null) {
                    updated = true;

                    sysLogService.recordLog("User :" + userid + "不存在，自动回收其访问权限",null,"处理僵尸用户", 0);
                    this.remove(new QueryWrapper<PtrUserEndpoint>()
                            .eq("user_id", userid));
                    it.remove();
                }
            }
            // 如果执行过删除，则更新ptr
            if (updated) {
                updateUserAccessOfPtr(e);
            }
        });

        return new PriorityQueue<>(this.list());
    }

    @Override
    @Transactional
    public void deleteItem(Checkable item) {
        log.info("执行删除：" + item);
        //sysLogService.recordLog("执行删除：" + item, null, )
        PtrUserEndpoint userEndpoint = this.getById(item.getId());
        PtrEndpoint e = ptrEndpointService.getPtrEndpointById(userEndpoint.getEndpointId());
        e.getUserIds().remove(userEndpoint.getUserId());
        boolean res = this.remove(new QueryWrapper<PtrUserEndpoint>().eq("id", item.getId()));

        if (!res) {
            throw new RuntimeException("删除失败！" + "  id：" + item.getId() + "detail：" + item);
        }

        updateUserAccessOfPtr(e);
    }

    public void updateUserAccessOfPtr(PtrEndpoint endpoint) {
        String json = CommonUtils.portainerFormatWrapper(endpoint.getUserIds());

        try {
            Response response = portainerConnector.putRequest("/endpoints/" + endpoint.getId(), json);
            log.info("Portainer response: " + response.toString());
            if (response.code() != 200) {
                assert response.body() != null;
                throw new PortainerException(response.body().string(), null, response.code());
            }

            response.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }


}
