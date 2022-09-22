package com.io.portainer.service.sys.impl;

import cn.hutool.http.HttpException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ConstValue;
import com.io.core.mapper.SysUserMapper;
import com.io.portainer.common.timer.Checkable;
import com.io.portainer.common.timer.RegularService;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.connect.PortainerConnector;
import com.io.portainer.common.utils.connect.WosSysConnector;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.mapper.SysWaitListMapper;
import com.io.portainer.mapper.sys.SysCheckListMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import com.io.portainer.service.sys.SysCheckListService;
import com.io.portainer.service.sys.SysWaitListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
    PtrUserService ptrUserService;

    @Autowired
    SysCheckListMapper sysCheckListMapper;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    PortainerConnector portainerConnector;

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    @Autowired
    WosSysConnector wosSysConnector;

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public PriorityQueue<Checkable> updateAll() {
        // 获取等待队列中所有项目
        List<SysWaitList> waits = this.list();
        // 获取各节点信息
        List<PtrEndpoint> endpoints = ptrEndpointService.getPtrEndpoints();


        for (Integer resourceType : CommonUtils.resourceTypeCodes()) {
            List<PtrEndpoint> epList = new ArrayList<>();
            PriorityQueue<SysWaitList> queue = new PriorityQueue<>();

            for (PtrEndpoint ep : endpoints) {
                if (ep.available(resourceType))
                    epList.add(ep);
            }

            for (SysWaitList wl : waits) {
                if (wl.getResourceType().equals(resourceType))
                    queue.add(wl);
            }
            // 如果不为独占型，则按剩余空间排序，负载均衡
            if (!resourceType.equals(ConstValue.SINGLE_RESOURCE))
                epList.sort(Comparator.comparing(PtrEndpoint::getSpace));

            for (int i=epList.size()-1; i>=0; i--) {
                PtrEndpoint ep = epList.get(i);
                SysWaitList peek = queue.peek();
                if (peek == null) {
                    break;
                }
                queue.remove();
                geEndPointAccess(peek, ep);
            }
        }
        return new PriorityQueue<>();
    }

    @Override
    @Transactional
    public void deleteItem(Checkable item) {
    }

    @Transactional
    @Deprecated
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
//                    throw new PortainerException(info);
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
    void geEndPointAccess(SysWaitList waitList, PtrEndpoint endpoint) {
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
                // TODO: 2022/7/25 新建portainer连接异常
                throw new HttpException(e.getMessage());
            }

            assert response != null;
            if (response.code() != 200) {
                // TODO：通知管理员
                String info = null;
                try {
                    info = response.body().string();
                } catch (IOException e) {
                    log.error(e.getMessage());
                    message = "Portainer 连接异常";
                    throw new HttpException(e.getMessage());
                }
                throw new RuntimeException(info);
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

        // 如果成功，给工单系统中对应用户发送信息
        if (isSuccess) {
            sentMessage(checkList, endpoint);
            log.info("处理完成");
        }
        else{
            log.error("处理失败");
            sendErrorMessage(checkList);
        }
    }

    private void sendErrorMessage(SysCheckList checkList) {

        if (!checkList.getType().equals(ConstValue.ERROR_LIST_TYPE)) {
            checkList.setType(ConstValue.ERROR_LIST_TYPE);
        }

        WosMessageDto message = new WosMessageDto();
        StringBuilder sb = new StringBuilder();

        PtrUser ptrUser = Objects.requireNonNull(ptrUserService.getById(checkList.getRelatedUserId()),
                "用户（id为:" + checkList.getRelatedUserId() +") 不存在，可能已被删除");;
        List<Long> wosIds = sysUserMapper.getOperatorsWosIds();

        message.setTitle("Gpu管理系统自动处理出现异常");
        sb.append("发生时间：")
                .append(LocalDateTime.now())
                .append("故障类型： 处理等待队列时异常")
                .append("详细信息：")
                .append(checkList.getMessage());
        message.setDescription(sb.toString());

        wosIds.forEach(i -> {
            message.setReceiver(i);
            wosSysConnector.asyncSendErrorMessage(message);
        });
    }

    private void sentMessage(SysCheckList checkList, PtrEndpoint endpoint) {
        WosMessageDto message = new WosMessageDto();
        StringBuilder sb = new StringBuilder();

        PtrUser user = Objects.requireNonNull(ptrUserService.getById(checkList.getRelatedUserId()),
                "用户（id为:" + checkList.getRelatedUserId() +") 不存在，可能已被删除");

        message.setReceiver(user.getWosId());
        message.setTitle("资源调度完成");
        sb.append("您申请的GPU资源----\n 名称：")
                .append(endpoint.getName())
                .append("\n")
                .append("url: ")
                .append(endpoint.getUrl())
                .append("\n")
                .append("已完成调度")
        ;
        message.setDescription(sb.toString());
        wosSysConnector.asyncSendMessage(message);
    }

}
