package com.io.portainer.common.exception;

public class ApplyConflictedException extends RuntimeException{
    public ApplyConflictedException(String message) {
        super(message);
    }
}
