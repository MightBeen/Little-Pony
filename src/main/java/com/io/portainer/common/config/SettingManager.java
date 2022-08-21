package com.io.portainer.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 管理系统的配置
 */
@Component
public class SettingManager {
    private SysSetting currentSetting;

    @Autowired
    DefaultSetting defaultSetting;


    /**
     * 返回当前设置的拷贝
     * @return
     */
    public SysSetting getCurrentSetting(){
        if (currentSetting == null){
            currentSetting = defaultSetting;
        }
        FlexibleSetting clonedSetting = new FlexibleSetting();
        clonedSetting.fieldInject(currentSetting);
        return clonedSetting;
    }

    public void toDefault() {
        this.currentSetting = defaultSetting;
    }

    public void setSetting(FlexibleSetting newSetting){
        SysSetting currentSetting = this.getCurrentSetting();
        currentSetting.fieldInject(newSetting);
        this.currentSetting = currentSetting;
    }

    /**
     * 初始化时调用，检查所需参数是否完善
     */
    public void checkSetting() {
        SysSetting currentSetting = this.getCurrentSetting();
        Field[] fields = SysSetting.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.get(currentSetting) == null) {
                    throw new SettingLostException("所需配置不完善：" + field.getName());
                }
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
