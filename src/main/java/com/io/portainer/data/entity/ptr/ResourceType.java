package com.io.portainer.data.entity.ptr;

import com.io.core.common.wrapper.ConstValue;

public enum ResourceType {
    GROUP_RESOURCE(ConstValue.GROUP_RESOURCE),
    SINGLE_RESOURCE(ConstValue.SINGLE_RESOURCE)
    ;

    public final Integer code;

    ResourceType(Integer code){
        this.code = code;
    }
}
