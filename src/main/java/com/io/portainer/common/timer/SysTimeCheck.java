package com.io.portainer.common.timer;


/**
 * 继承该接口，并注入spring ioc容器即可注册定期执行业务
 */
public interface SysTimeCheck {
    /**
     * 成功返回true，失败返回false
     */
    boolean execute();

    boolean isEnabled();

    Long getInterval();

    void OnException(Throwable throwable);
}
