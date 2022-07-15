package com.io.init;

import com.io.portainer.common.config.SettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 管理系统初始化时的执行逻辑
 */

@Component
public class SysStartRegister {

    @Autowired
    SysInitStarter sysInitStarter;

    @Autowired
    SettingManager settingManager;

    public SysStartRegister init(ApplicationContext appContext) {
        SysTimerStarter.start(appContext, settingManager);
        sysInitStarter.systemInit();
        return this;
    }

}
