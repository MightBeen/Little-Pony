package com.io.portainer.common.exception;

import com.io.portainer.data.entity.sys.SysCheckList;

public class PortainerException extends RuntimeException {
    private SysCheckList checkList;
    private int code;
    public PortainerException(String message, SysCheckList checkList, int code) {
        super(message);
        this.checkList = checkList;
        this.code = code;
    }

    public SysCheckList getCheckList() {
        return checkList;
    }

    public int getCode() {
        return code;
    }

    public void setCheckList(SysCheckList checkList) {
        this.checkList = checkList;
    }
}
