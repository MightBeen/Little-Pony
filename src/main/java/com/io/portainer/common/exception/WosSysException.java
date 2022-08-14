package com.io.portainer.common.exception;

/**
 * 因工单系统方面错误引发的异常，错误返回码为 4xx
 */
public class WosSysException extends RuntimeException {
    public final int code;

    public WosSysException(String message, int code) {
        super(message);
        this.code = code;
    }
}
