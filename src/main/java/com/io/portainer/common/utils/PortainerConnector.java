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
public class PortainerConnector {
    private String BaseUrl;
    private String apiKey;

    @Autowired
    private SettingManager settingManager;

//    PortainerConnector() {
//        this.BaseUrl = settingManager.getCurrentSetting().getPortainerUrl();
//        this.apiKey = settingManager.getCurrentSetting().getPortainerAccessToken();
//    }

    private Response request(String method, String url, String body) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = body == null ? null : RequestBody.create(mediaType, body);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(settingManager.getCurrentSetting().getPortainerUrl() + url)
                .method(method, requestBody)
                .addHeader("X-API-Key",  settingManager.getCurrentSetting().getPortainerAccessToken())
                .build();

        return client.newCall(request).execute();
    }

    public Response getRequest(String url) throws IOException{
        return request("GET", url ,null);
    }

    public Response postRequest(String url, String json) throws IOException{
        return request("POST", url ,json);
    }

    public Response putRequest(String url, String json) throws IOException{
        return request("PUT", url ,json);
    }

    public Response deleteRequest(String url) throws IOException{
        return request("DELETE", url ,null);
    }
}
