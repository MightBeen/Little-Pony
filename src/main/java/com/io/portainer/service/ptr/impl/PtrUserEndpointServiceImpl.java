package com.io.portainer.service.ptr.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.RegularService;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.mapper.ptr.PtrUserEndpointMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;

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
    PortainerConnector portainerConnector;

    /**
     * 顺便会更新节点
     */
    @Override
    public PriorityQueue<Checkable> updateAll() {
        // 先执行节点的更新
        ptrEndpointService.updatePtrEndpointsDataFromPtr();

        // 获取更新后的数据
        List<PtrUserEndpoint> list = this.list();
        PriorityQueue<Checkable> res = new PriorityQueue<>();

        res.addAll(list);

        return res;
    }

    @Override
    @Transactional
    public void deleteItem(Checkable item) {
        log.info("执行删除：" + item);
        PtrUserEndpoint userEndpoint = this.getById(item.getId());
        PtrEndpoint e = ptrEndpointService.getPtrEndpointById(userEndpoint.getEndpointId());
        e.getUserIds().remove(userEndpoint.getUserId());
        boolean res = this.remove(new QueryWrapper<PtrUserEndpoint>().eq("id", item.getId()));

        if (!res) {
            throw new RuntimeException("删除失败！" + "  id："+ item.getId() + "detail：" + item);
        }


        String json = CommonUtils.portainerFormatWrapper(e.getUserIds());

        try {
            Response response = portainerConnector.putRequest("/endpoints/" + e.getId(), json);
            log.info("Portainer response: " + response.toString());
            log.info(response.body().string());
            response.close();
            if (response.code() != 200){
                throw new PortainerException(response.toString() + "" +response.body().string());
            }
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
