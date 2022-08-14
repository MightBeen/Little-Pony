package com.io.portainer.common.exception;

import cn.hutool.http.HttpException;

/**
 * 工单或portainer等系统api连接异常
 */
public class ApiConnectionException extends HttpException {
    public ApiConnectionException(String message) {
        super(message);
    }
}
