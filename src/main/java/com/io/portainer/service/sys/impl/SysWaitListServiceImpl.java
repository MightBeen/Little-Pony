package com.io.portainer.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.RegularService;
import com.io.portainer.common.check.components.SysDataCache;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.mapper.SysWaitListMapper;
import com.io.portainer.mapper.sys.SysCheckListMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.sys.SysCheckListService;
import com.io.portainer.service.sys.SysWaitListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.util.resources.cldr.lg.CurrencyNames_lg;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-15
 */
@Service
@Slf4j
public class SysWaitListServiceImpl extends ServiceImpl<SysWaitListMapper, SysWaitList>
        implements SysWaitListService, RegularService<SysWaitList> {

    @Autowired
    @Lazy
    SysCheckListService sysCheckListService;

    @Autowired
    SysCheckListMapper sysCheckListMapper;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    PortainerConnector portainerConnector;

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    SysDataCache dataCache;


    @Override
    @Transactional
    public PriorityQueue<Checkable> updateAll() {
        // 获取等待队列中所有项目
        List<SysWaitList> waits = this.list();
        // 获取各节点信息
        List<PtrEndpoint> endpoints = ptrEndpointService.getPtrEndpoints();


        for (Integer resourceType : CommonUtils.resourceTypeCodes()) {
            List<PtrEndpoint> eplist = new ArrayList<>();
            PriorityQueue<SysWaitList> queue = new PriorityQueue<>();

            for (PtrEndpoint ep : endpoints) {
                if (ep.available(resourceType))
                    eplist.add(ep);
            }

            for (SysWaitList wl : waits) {
                if (wl.getResourceType().equals(resourceType))
                    queue.add(wl);
            }

            for (PtrEndpoint ep : eplist) {
                SysWaitList peek = queue.peek();
                if (peek == null) {
                    break;
                }
                queue.remove();
                geEndPointAccess(peek, ep);
            }
        }

//        // 查看对应资源是否由可用。如果可用，直接设为过期
//    label:
//        for (SysWaitList item : lists) {
//            Integer resourceType = item.getResourceType();
//            for (PtrEndpoint ep : endpoints) {
//                if (ep.available(resourceType)) {
////                    getAccessForUser(item);
////                    lists.remove(item);
//                    item.setExpired(LocalDateTime.now());
//                    break label;
//                }
//            }
//        }

//        // 如果对应资源已满，则将过期时间设置为对应资源使用用户中最近过期时间
//        for (SysWaitList i : lists) {
//            Integer resourceType = i.getResourceType();
//            List<LocalDateTime> expiredDates = null;
//            if (ConstValue.SINGLE_RESOURCE.equals(resourceType) || ConstValue.GROUP_RESOURCE.equals(resourceType)) {
//                expiredDates = sysCheckListMapper.getExpiredDatesByType(ConstValue.SINGLE_RESOURCE);
//            } else {
//                log.error("资源类型不存在：" + resourceType);
//                continue;
//            }
//
//            // 如果不存在目标资源使用，则说明目标资源已空闲，或出现异常。立即设置为过期，交由删除业务处理
//            if (expiredDates == null || expiredDates.size() == 0) {
//                i.setExpired(LocalDateTime.now());
//                continue;
//            }
//            Optional<LocalDateTime> min = expiredDates.stream().min(LocalDateTime::compareTo);
//            i.setExpired(min.get());
//        }

        return new PriorityQueue<>();
    }

    @Override
    @Transactional
    public void deleteItem(Checkable item) {
        SysWaitList waitList = this.getById(item.getId());
        getAccessForUser(waitList);
    }

    @Transactional
    boolean getAccessForUser(SysWaitList waitList) {
        log.info("正在处理waitList：" + waitList);

        boolean isSuccess = false;

        Integer resourceType = waitList.getResourceType();

        List<PtrEndpoint> endpoints = ptrEndpointService.getPtrEndpoints();

        String message = "资源类型异常";

        for (PtrEndpoint ep : endpoints) {
            if (ep.available(resourceType)) {
                List<Long> userIds = ep.getUserIds();
                userIds.add(waitList.getRelatedUserId());
                Response response = null;

                try {
                    response = portainerConnector.putRequest("/endpoints/" + ep.getId(), CommonUtils.portainerFormatWrapper(userIds));
                } catch (IOException e) {
                    log.error(e.getMessage());
                    message = "Portainer 连接异常";
                    break;
                }

                assert response != null;
                if (response.code() != 200) {
                    // TODO：通知管理员
                    String info = response.toString();
                    response.close();
                    throw new PortainerException(info);
                }

                // 填入ue关系表
                PtrUserEndpoint chain = new PtrUserEndpoint();
                chain.setCreated(LocalDateTime.now());
                chain.setEndpointId(ep.getId());
                chain.setUserId(waitList.getRelatedUserId());

                // TODO ： 设置过期时间
                chain.setExpired(LocalDateTime.now().plusDays(waitList.getApplyDays()));
                ptrUserEndpointService.save(chain);

                isSuccess = true;

                break;
            }
        }

        SysCheckList checkList = sysCheckListService
                .getOne(new QueryWrapper<SysCheckList>().eq("wait_list_id", waitList.getId()));
        checkList.setUpdated(LocalDateTime.now());

        if (isSuccess) {
            checkList.setStatus(1);
        } else {
            // 如果不存在，则说明出现异常
            checkList.setType(ConstValue.ERROR_LIST_TYPE);
            // TODO: 2022/7/15 记入日志，给管理员发送消息
            log.error(message + "： " + resourceType + "-----" + waitList);
            log.error(message + " ----" + endpoints);
        }

        // 在数据库中删除这条记录
        this.removeById(waitList);
        // 同时更新checkList
        sysCheckListService.updateById(checkList);

        // TODO: 2022/7/18 给工单系统中对应用户发送信息
        log.info("处理完成");
        return isSuccess;
    }

    @Transactional
    boolean geEndPointAccess(SysWaitList waitList, PtrEndpoint endpoint) {
        log.info("正在处理waitList：" + waitList + "至 \t" + endpoint.getId() + "\t" +endpoint.getName());

        boolean isSuccess = false;

        Integer resourceType = waitList.getResourceType();

        List<PtrEndpoint> endpoints = ptrEndpointService.getPtrEndpoints();

        String message = "资源类型异常";

        if (endpoint.available(resourceType)) {
            List<Long> userIds = endpoint.getUserIds();
            userIds.add(waitList.getRelatedUserId());
            Response response = null;

            try {
                response = portainerConnector.putRequest("/endpoints/" + endpoint.getId(), CommonUtils.portainerFormatWrapper(userIds));
            } catch (IOException e) {
                log.error(e.getMessage());
                message = "Portainer 连接异常";
            }

            assert response != null;
            if (response.code() != 200) {
                // TODO：通知管理员
                String info = response.toString();
                response.close();
                throw new PortainerException(info);
            }

            // 填入ue关系表
            PtrUserEndpoint chain = new PtrUserEndpoint();
            chain.setCreated(LocalDateTime.now());
            chain.setEndpointId(endpoint.getId());
            chain.setUserId(waitList.getRelatedUserId());

            // TODO ： 设置过期时间
            chain.setExpired(LocalDateTime.now().plusDays(waitList.getApplyDays()));
            ptrUserEndpointService.save(chain);

            isSuccess = true;
        }

        SysCheckList checkList = sysCheckListService
                .getOne(new QueryWrapper<SysCheckList>().eq("wait_list_id", waitList.getId()));
        checkList.setUpdated(LocalDateTime.now());

        if (isSuccess) {
            checkList.setStatus(1);
        } else {
            // 如果不存在，则说明出现异常
            checkList.setType(ConstValue.ERROR_LIST_TYPE);
            // TODO: 2022/7/15 记入日志，给管理员发送消息
            log.error(message + "： " + resourceType + "-----" + waitList);
            log.error(message + " ----" + endpoints);
        }

        // 在数据库中删除这条记录
        this.removeById(waitList);
        // 同时更新checkList
        sysCheckListService.updateById(checkList);

        // TODO: 2022/7/18 给工单系统中对应用户发送信息
        log.info("处理完成");
        return isSuccess;
    }

}
