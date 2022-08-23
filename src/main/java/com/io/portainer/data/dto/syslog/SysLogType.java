package com.io.portainer.data.dto.syslog;

/**
 * 记录日志类型 && code 对应数据库里的 の sys_log の type
 *
 */
public enum SysLogType {
    /**
     * 数据库中并没有“0”
     */
    ALL_SYSLOG("ALL_SYSLOG",0),
    SYS_HANDLE("SYS_HANDLE",1),
    SYS_EXCEPTION("SYS_EXCEPTION",2),
    OPERATOR_HANDLE("OPERATOR_HANDLE",3),
    ;
    public final String name;
    public final Integer code;

    SysLogType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
}
