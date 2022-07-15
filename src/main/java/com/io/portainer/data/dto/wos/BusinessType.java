package com.io.portainer.data.dto.wos;

public enum BusinessType {
    GPU_RENEWAL("GPU_RENEWAL"),
    GPU_APPLY("GPU_APPLY"),
    ;

    public final String code;

    BusinessType(String code) {
        this.code = code;
    }
}
