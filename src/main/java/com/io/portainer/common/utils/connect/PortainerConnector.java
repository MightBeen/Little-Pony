package com.io.portainer.common.utils.connect;


import com.io.portainer.common.config.SettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortainerConnector extends ApiConnector{

    @Autowired
    private SettingManager settingManager;

    @Override
    protected String connectionName() {
        return "Portainer";
    }

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
