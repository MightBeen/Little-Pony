package com.io.portainer.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Data
public abstract class SysSetting {
    protected String portainerAccessToken;

    protected String portainerUrl;

    protected String wosAccessToken;

    protected String wosUrl;

    protected Boolean AutoCheck;

    protected Long timerCheck;

    protected Long timerGroupGpuExpire;

    protected Long timerSingleGpuExpire;

    protected Long timerGroupGpuClear;

    protected Long timerSingleGpuClear;



    public static void fieldInject(SysSetting oldSetting, SysSetting newSetting) {
        Field[] fields = SysSetting.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(newSetting);
                if (value != null) {
                    field.set(oldSetting, value);
                }
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void fieldInject(SysSetting newSetting) {
        SysSetting.fieldInject(this, newSetting);
    }
}
