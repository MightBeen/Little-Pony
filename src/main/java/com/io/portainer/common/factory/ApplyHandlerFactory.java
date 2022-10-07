package com.io.portainer.common.factory;

import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.exception.WosSysException;
import com.io.portainer.common.factory.apply.BusinessHandler;
import com.io.portainer.data.dto.wos.WosUser;

import java.io.IOException;
import java.util.HashMap;

/**
 * 业务工厂，用于处理各类申请
 */
public class ApplyHandlerFactory {
    public static final HashMap<String, BusinessHandler> handlerMap = new HashMap<String, BusinessHandler>();

    public static ResultWrapper handleApply(WosUser wosUser) throws IOException {
        BusinessHandler handler = handlerMap.get(wosUser.getBusinessType());
        if (handler == null)
            throw new WosSysException("不支持的业务类型：" + wosUser.getBusinessType(), 411);

        return handler.process(wosUser);
    }
}
