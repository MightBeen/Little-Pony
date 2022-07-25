package com.io.portainer.common.utils;

import cn.hutool.http.HttpException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.exception.WosSysException;
import com.io.portainer.data.dto.wos.ResponseDto;
import com.io.portainer.data.dto.wos.WosMessageDto;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class WosSysConnector extends ApiConnector{

    @Autowired
    SettingManager settingManager;

    @Override
    protected String getApiKey() {
        return settingManager.getCurrentSetting().getWosAccessToken();
    }

    @Override
    protected String getBaseUrl() {
        return settingManager.getCurrentSetting().getWosUrl();
    }

    @Override
    protected String getApiKeyName() {
        return "API-KEY";
    }

    /**
     * 异步向工单系统发送信息
     */
    public void asyncSendMessage(WosMessageDto message) {
        // TODO: 2022/7/25 利用线程池优化 
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(message, false);
            }
        });
        thread.start();
    }

    /**
     * 向工单系统发送信息
     * @param message 信息内容，包括标题和主题
     * @param errorOccurred 是否为发生异常时向操作员发送的信息。如果是，则再次出现异常时则该为普通异常，否则会陷入死循环
     */
    public void sendMessage(WosMessageDto message, boolean errorOccurred) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            Response response = this.postRequest("/gpu/api/message", json);
            int code = response.code();

            if (code != 200) {
                InputStream body = response.body().byteStream();
                ResponseDto responseDto = null;
                try {
                    responseDto = objectMapper.readValue(body, ResponseDto.class);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (!errorOccurred)
                    throw new WosSysException(responseDto.getMessage(), code);
                else
                    throw new RuntimeException(responseDto.getMessage());
            }

            response.close();
        } catch (IOException e) {
            throw new HttpException("工单系统连接异常 " + e.getMessage());
        }
    }

    /**
     *     异步向管理员发送异常信息
     */
    public void asyncSendErrorMessage(WosMessageDto message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendErrorMessage(message);
            }
        });
       thread.start();
    }

    public void sendErrorMessage(WosMessageDto message) {
        sendMessage(message, true);
    }
}
