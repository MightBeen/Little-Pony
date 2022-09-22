package com.io.portainer.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


/**
 * 系统默认配置，从配置文件中获取
 */
@Component
@ConfigurationProperties(prefix = "sys-default-setting")
public class DefaultSetting extends SysSetting {
}
