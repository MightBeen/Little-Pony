package com.io.portainer.service.ptr;

import com.baomidou.mybatisplus.extension.service.IService;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;

import java.io.IOException;
import java.util.List;

public interface PtrUserService extends IService<PtrUser> {

    // TODO: 添加PtrEndPointService分页查询

    /**
     * 从portainer中获取用户列表
     */
    List<PtrUser> getUsersFromPtr() throws IOException;

    /**
     * 从portainer中通过id获取用户
     */
    PtrUser getOneUserFromPtr(Long id) throws IOException;

    /**
     * 从管理系统数据库中获取系统用户
     */
    List<PtrUser> getUsersFromSys();

    /**
     * 从portainer中获取并更新管理系统中用户
     */
    List<PtrUser> updateUsersFromPtr();

    /**
     * 根据资源类型自动添加用户访问权限
     * @return : 用户是否已成功添加
     */
    boolean getEndPointAccessById(PtrUser ptrUser, int resourceType, int day) throws IOException;

    /**
     * 从申请中添加用户
     */
    PtrUser addPtrUserToPtr(PtrUser u) throws IOException;
}
