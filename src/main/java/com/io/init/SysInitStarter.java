package com.io.init;

import com.io.portainer.common.check.components.SysDataCache;
import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SysInitStarter {


    @Autowired
    PtrUserService ptrUserService;

    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    SysDataCache dataCache;

    public void systemInit(){
        initializeUserDataFromPortainer();
        initializeDataCacheManager();
    }

    /**
     * 从portainer 中更新数据到管理系统数据库
     */
    @Deprecated
    private void initializeUserDataFromPortainer() {
    }

    /**
     * 初始化缓存管理配置
     */
    private void initializeDataCacheManager(){
        dataCache.init();
    }
}
