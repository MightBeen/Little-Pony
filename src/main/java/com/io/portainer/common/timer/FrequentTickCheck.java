package com.io.portainer.common.timer;

/**
 * 会被频繁调用的计时器组件
 */
public interface FrequentTickCheck extends SysTimeCheck {
    @Override
    default Long getInterval() {
        return 1000L;
    }
}
