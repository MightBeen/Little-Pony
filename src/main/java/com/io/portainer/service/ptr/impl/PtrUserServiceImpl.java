package com.io.portainer.service.ptr.impl;

import cn.hutool.http.HttpException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.common.exception.ApplyConflictedException;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.mapper.ptr.PtrUserMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.portainer.service.sys.SysCheckListService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class PtrUserServiceImpl extends ServiceImpl<PtrUserMapper, PtrUser> implements PtrUserService {

    @Autowired
    PortainerConnector portainerConnector;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    SysCheckListService sysCheckListService;

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    private final String baseUrl = "/users";

    /**
     * 从portainer中获取用户列表
     */
    @Override
    public List<PtrUser> getUsersFromPtr() throws IOException {
        List<PtrUser> users = new ArrayList<PtrUser>();
        Response response = portainerConnector.getRequest("/users");

        if (response.code() == 404) {
            return null;
        } else if (response.code() == 200) {
            PtrJsonParser<PtrUser> parser = new PtrJsonParser<PtrUser>(PtrUser.class);
            assert response.body() != null;
            users = parser.parseJsonArray(response.body().string());
        }

        return users;
    }

    /**
     * 从portainer中通过id获取用户
     */
    @Override
    public PtrUser getOneUserFromPtr(Long id) throws IOException {
        Response response = portainerConnector.getRequest(baseUrl + "/" + id);

        PtrUser ptrUser = null;

        if (response.code() == 404) {
            System.out.println(response);
            return null;
        } else if (response.code() == 200) {
            PtrJsonParser<PtrUser> parser = new PtrJsonParser<PtrUser>(PtrUser.class);
            assert response.body() != null;
            ptrUser = parser.parseJson(response.body().string());
        }

        return ptrUser;
    }

    /**
     * 从管理系统数据库中获取系统用户
     */
    @Override
    public List<PtrUser> getUsersFromSys() {
        return this.list();
    }

    /**
     * 从portainer中获取并更新管理系统中数据库用户表。初始化时会调用
     */
    @Override
    @Transactional
    public List<PtrUser> updateUsersFromPtr() {
        Response response = null;
        // 用于返回
        List<PtrUser> newUsers = null;

        // 从管理系统中获取的用户
        List<PtrUser> dataBaseUserList;

        // 从portainer获取的用户
        List<PtrUser> ptrUserList;

        try {
            response = portainerConnector.getRequest(baseUrl);

            if (response.code() == 200) {
                PtrJsonParser<PtrUser> parser = new PtrJsonParser<>(PtrUser.class);
                ResponseBody body = response.body();
                assert body != null;
                ptrUserList = parser.parseJsonArray(body.string());

                for (PtrUser ptu : ptrUserList) {
                    ptu.setCreated(LocalDateTime.now());
                }
            } else {
                throw new HttpException("Portainer 连接异常: " + response.code());
            }

            dataBaseUserList = this.list();

            List<Field> updatableFields = new PtrJsonParser<PtrUser>(PtrUser.class).getUpdatableFields();

            // 将数据合并
            for (PtrUser ptrUser : ptrUserList) {
                dataBaseUserList.forEach(u -> {
                    if (u.getId().equals(ptrUser.getId())) {
                        for (Field field : updatableFields) {
                            try {
                                field.setAccessible(true);
                                field.set(ptrUser, field.get(u));
                                field.setAccessible(false);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                System.out.println(e.getMessage());
                            }
                        }
                        ptrUser.setUpdated(LocalDateTime.now());
                    }
                });
            }
            newUsers = ptrUserList;

            List<Long> ids = new ArrayList<>();
            dataBaseUserList.forEach(u -> ids.add(u.getId()));
            // 移除旧数据
            this.removeByIds(ids);

            this.saveBatch(newUsers);

        } catch (IOException | HttpException e) {
//            e.printStackTrace();
            log.error("Error getting users from portainer");
            return null;
        }

        return newUsers;
    }


    /**
     * 根据资源类型自动添加用户访问权限
     */
    @Override
    @Transactional
    public boolean getEndPointAccessById(PtrUser ptrUser, int resourceType, int day) throws IOException {
        if (ptrUser == null) throw new IllegalArgumentException("ptrUser cannot be null");
        if (ptrUser.getRole() == 1) {
            throw new IllegalArgumentException("申请用户不能为管理员");
        }

        // 检查是否重复申请
        List<PtrUserEndpoint> user_ids01 = ptrUserEndpointService.list(new QueryWrapper<PtrUserEndpoint>().eq("user_id", ptrUser.getId()));
        if (!user_ids01.isEmpty()) {
            throw new ApplyConflictedException("重复申请 ：" + ptrUser.getUsername());
        }
        // 检查等待队列
        List<SysCheckList> user_ids02 = sysCheckListService.list(new QueryWrapper<SysCheckList>().eq("type", 0));

        for (SysCheckList user_id : user_ids02) {
            if(ptrUser.getId().equals(user_id.getRelatedUserId()))
                throw new IllegalArgumentException("已在等待队列中：" + ptrUser.getUsername());
        }

        if (resourceType == ConstValue.GROUP_RESOURCE) {
            // todo: implements this method;

        } else if (resourceType == ConstValue.SINGLE_RESOURCE) {
            List<PtrEndpoint> target = ptrEndpointService.getPtrEndpoints();

            for (PtrEndpoint endpoint : target) {
                // 当前没有用户在使用，且处于开启状态的资源筛选
                // TODO: 将这一部分也添加仅队列，由队列进行分配
                List<Long> userIds = endpoint.getUserIds();

                if (userIds.size() == 0 && endpoint.getStatus() == 1) {

                    userIds.add(ptrUser.getId());
                    Response response = portainerConnector.putRequest("/endpoints/" + endpoint.getId(), CommonUtils.portainerFormatWrapper(userIds));
                    if (response.code() != 200) {
                        // TODO：填入队列并联系管理员
                        throw new PortainerException(response.toString());
                    }
                    PtrUserEndpoint chain = new PtrUserEndpoint();
                    chain.setCreated(LocalDateTime.now());
                    chain.setEndpointId(endpoint.getId());
                    chain.setUserId(ptrUser.getId());

                    // TODO ： 设置过期时间
                    chain.setExpired(LocalDateTime.now().plusDays(day));
                    ptrUserEndpointService.save(chain);

                    return true;
                }
            }
            // 无符合条件资源，添入队列
            SysCheckList item = new SysCheckList();

            item.setCreated(LocalDateTime.now());
            item.setMessage(ptrUser.getRemark());
            item.setRelatedUserId(ptrUser.getId());
            item.setType(0);
            sysCheckListService.AddItemToCheckList(item);
        } else {
            throw new IllegalArgumentException("资源类型不可用: " + resourceType);
        }

        return false;
    }




    @Override
    @Transactional
    public PtrUser addPtrUserToPtr(PtrUser u) throws IOException {

        // TODO 完成自定义序列化
        PtrJsonParser<PtrUser> parser = new PtrJsonParser<PtrUser>(PtrUser.class);
        String body = parser.getObjectMapper().writeValueAsString(u);
//        System.out.println(body);
        Response response = portainerConnector.postRequest(baseUrl, body);

        String responseBody = response.body().string();

        PtrUser builtUser = parser.parseJson(responseBody);

        u.setId(builtUser.getId());

        // TODO: catch 409异常
        if (response.code() != 200) {
            // TODO: 给管理员发送异常信息
            PortainerException e = new PortainerException(response.toString()+"\n" + responseBody);

            // 将异常加入checkList
            SysCheckList item = new SysCheckList();

            item.setCreated(LocalDateTime.now());
            item.setMessage(e.getMessage());
            item.setRelatedUserId(u.getId());
            item.setType(response.code());
            sysCheckListService.AddItemToCheckList(item);

            throw e;
        }
        u.setCreated(LocalDateTime.now());
        save(u);
        return u;
    }
}
