package com.io.portainer.common.utils;


import com.io.portainer.common.config.SettingManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PortainerConnector extends ApiConnector{

    @Autowired
    private SettingManager settingManager;

    @Override
    protected String getApiKey() {
        return settingManager.getCurrentSetting().getPortainerAccessToken();
    }

    @Override
    protected String getBaseUrl() {
        return settingManager.getCurrentSetting().getPortainerUrl();
    }

    @Override
    protected String getApiKeyName() {
        return "X-API-KEY";
    }
}
