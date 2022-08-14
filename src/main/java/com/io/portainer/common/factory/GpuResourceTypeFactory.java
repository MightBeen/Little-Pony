package com.io.portainer.common.factory;

import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.common.exception.WosSysException;
import com.io.portainer.data.entity.ptr.ResourceType;

public class GpuResourceTypeFactory {

    public static void checkResourceTypeCode(Integer resourceType) {
        for(ResourceType type : ResourceType.values()) {
            if (type.code.equals(resourceType))
                return;
        }
        throw new WosSysException("申请中含有不可用资源类型码：" + resourceType, 410);
    }
}
