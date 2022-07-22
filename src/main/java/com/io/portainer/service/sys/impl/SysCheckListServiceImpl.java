package com.io.portainer.service.sys.impl;

import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.mapper.sys.SysCheckListMapper;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.sys.SysCheckListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.portainer.service.sys.SysWaitListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
@Service
public class SysCheckListServiceImpl extends ServiceImpl<SysCheckListMapper, SysCheckList>
        implements SysCheckListService{

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    SysWaitListService sysWaitListService;


    /**
     * 将信息加入至等待队列和代办队列
     *
     * @param item
     * @param resourceType
     * @param day
     * @return
     */
    @Override
    @Transactional
    public SysCheckList AddItemToWaitList(@NotNull SysCheckList item, @NotNull Integer resourceType, @NotNull Integer day, @NotNull Long jobId) {

        Long waitListID = System.currentTimeMillis() + (long)(100 * Math.random());
        SysWaitList waitList = new SysWaitList(item, resourceType, day, jobId);


        item.setType(ConstValue.WAIT_LIST_TYPE);
        item.setWaitListId(waitListID);

        waitList.setId(waitListID);
        waitList.setCreated(LocalDateTime.now());

        this.save(item);
        sysWaitListService.save(waitList);

        return item;
    }


}
