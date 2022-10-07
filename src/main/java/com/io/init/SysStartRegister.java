package com.io.init;

import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.timer.components.SysDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 管理系统初始化时的执行逻辑
 */
@Component
public class SysStartRegister {


    final
    SettingManager settingManager;

    final
    SysDataCache dataCache;

    public SysStartRegister(SettingManager settingManager, SysDataCache dataCache) {
        this.settingManager = settingManager;
        this.dataCache = dataCache;
    }

    public void init(ApplicationContext appContext) {

//        settingManager.checkSetting();

        // 初始化缓存
        dataCache.init();

        SysTimerStarter.start(appContext, settingManager);
    }

}
