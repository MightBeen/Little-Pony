package com.io.portainer.common.factory.apply;

import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.factory.ApplyHandlerFactory;
import com.io.portainer.data.dto.wos.WosUser;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 抽象的业务处理器。spring在创建其子类对象时会将其加入至工厂的集合中
 */
public abstract class BusinessHandler {
    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    PtrUserService ptrUserService;

    @Autowired
    PtrUserEndpointService ptrUserEndpointService;


    protected BusinessHandler() {
        ApplyHandlerFactory.handlerMap.put(getBusinessCode(), this);
    }

    abstract protected String getBusinessCode();

    public abstract ResultWrapper process(WosUser wosUser);
}
