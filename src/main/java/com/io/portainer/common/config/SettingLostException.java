package com.io.portainer.common.config;

/**
 * 必要配置丢失异常
 */
public class SettingLostException extends RuntimeException{
    public SettingLostException(String s) {
        super(s);
    }
}
