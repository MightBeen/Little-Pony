package com.io.portainer.common.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public abstract class ApiConnector {
    private Response request(String method, String url, String body) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = body == null ? null : RequestBody.create(mediaType, body);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(this.getBaseUrl() + url)
                .method(method, requestBody)
                .addHeader(this.getApiKeyName(),  this.getApiKey())
                .build();

        return client.newCall(request).execute();
    }

    protected abstract String getApiKey();

    protected abstract String getBaseUrl();

    protected abstract String getApiKeyName();


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
