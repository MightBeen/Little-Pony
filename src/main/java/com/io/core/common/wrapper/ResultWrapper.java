package com.io.core.common.wrapper;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultWrapper {

    private int code;
    private String msg;
    private Object data;

    public static ResultWrapper success(int code ,String msg, Object data ){
        return new ResultWrapper(code, msg, data);
    }
    public static ResultWrapper success(Object data ){
        return new ResultWrapper(200, "操作成功", data);
    }
    public static ResultWrapper success(String message, Object data ){
        return new ResultWrapper(200, message, data);
    }

    public static ResultWrapper fail(int code ,String msg, Object data ) {
        return new ResultWrapper(code, msg, data);
    }
    public static ResultWrapper fail(int code ,String msg) {
        return new ResultWrapper(code, msg, null);
    }
    public static ResultWrapper fail(String msg) {
        return new ResultWrapper(400, msg, null);
    }

    public static void writeMsg(HttpServletResponse response, String msg, boolean success) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        ResultWrapper fail = success ? ResultWrapper.success(msg) : ResultWrapper.fail(msg);

        outputStream.write(JSONUtil.toJsonStr(fail).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }

    public static void writeSuccessMsg(HttpServletResponse response, String msg) throws IOException {
        writeMsg(response, msg, true);
    }

    public static void writeFailedMsg(HttpServletResponse response, String msg) throws IOException {
        writeMsg(response, msg, false);
    }
}
