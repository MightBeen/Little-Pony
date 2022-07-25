package com.io.portainer.common.exception;

public class WosSysException extends RuntimeException {
    public final int code;

    public WosSysException(String message, int code) {
        super(message);
        this.code = code;
    }
}
