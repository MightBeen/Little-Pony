package com.io.portainer.common.config;

import lombok.Data;

@Data
public abstract class SysSetting {
    protected String portainerAccessToken;

    protected String portainerUrl;

    protected String wosAccessToken;

    protected String wosUrl;

    protected Long timerCheck;

    protected Long timerGroupGpuExpire;

    protected Long timerSingleGpuExpire;

    protected Long timerGroupGpuClear;

    protected Long timerSingleGpuClear;
}
