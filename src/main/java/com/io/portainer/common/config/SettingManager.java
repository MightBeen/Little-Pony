package com.io.portainer.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 管理系统的配置
 */
@Component
public class SettingManager {
    private SysSetting currentSetting;

    @Autowired
    DefaultSetting defaultSetting;

    @Autowired
    FlexibleSetting flexibleSetting;


    public SysSetting getCurrentSetting(){
        if (currentSetting == null){
            currentSetting = defaultSetting;
        }
        return currentSetting;
    }
}
