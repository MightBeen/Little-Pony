package com.io.portainer.service.ptr.impl;

import cn.hutool.http.HttpException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.RegularService;
import com.io.portainer.common.exception.ApplyConflictedException;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.mapper.ptr.PtrUserMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.portainer.service.sys.SysCheckListService;
import com.io.portainer.service.sys.SysWaitListService;
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
public class PtrUserServiceImpl extends ServiceImpl<PtrUserMapper, PtrUser>
        implements PtrUserService, RegularService<PtrUser> {

    @Autowired
    PortainerConnector portainerConnector;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    SysCheckListService sysCheckListService;

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    @Autowired
    SysWaitListService sysWaitListService;

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
     * 从portainer中获取并更新管理系统中数据库用户表。仅由UpdateMananger调用
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
        if (ptrUser == null || ptrUser.getJobId() == null) throw new IllegalArgumentException("ptrUser or jobId cannot be null");
        if (ptrUser.getRole() == 1) {
            throw new IllegalArgumentException("申请用户不能为管理员");
        }

        // 检查是否重复申请
        List<PtrUserEndpoint> user_ids01 = ptrUserEndpointService.list(new QueryWrapper<PtrUserEndpoint>().eq("user_id", ptrUser.getId()));
        if (!user_ids01.isEmpty()) {
            throw new ApplyConflictedException("重复申请 ：" + ptrUser.getUsername());
        }

        // 检查等待队列
        List<SysWaitList> waitList = sysWaitListService.list(new QueryWrapper<SysWaitList>().eq("job_id", ptrUser.getJobId()));

        // 检查申请资源类型是否有效
        CommonUtils.getCapacity(resourceType);

        if(waitList.size() > 0)
            // TODO: 新建一个异常类
            throw new IllegalArgumentException("已在等待队列中：" + ptrUser.getUsername());

        // 校验完成，添入队列
        SysCheckList item = new SysCheckList();

        item.setCreated(LocalDateTime.now());
        item.setMessage(ptrUser.getRemark());
        item.setRelatedUserId(ptrUser.getId());
        item.setRelatedResourceType(resourceType);
        sysCheckListService.AddItemToWaitList(item, resourceType, day, ptrUser.getJobId());

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
            item.setMessage(response.code() + e.getMessage());
            item.setRelatedUserId(u.getId());
            item.setType(Long.valueOf(ConstValue.ERROR_LIST_TYPE));
            sysCheckListService.save(item);

            response.close();
            throw e;
        }
        response.close();
        u.setCreated(LocalDateTime.now());
        save(u);
        return u;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 更新用户表
     * @return
     */
    @Override
    @Transactional
    public PriorityQueue<Checkable> updateAll() {
        List<PtrUser> ptrUsers = updateUsersFromPtr();
        // ptrUser 不需要定期删除，故直接返回空队列
        return new PriorityQueue<>();
    }

    @Override
    public void deleteItem(Checkable item) {
        log.warn("此方法不应该被调用");
    }
}
