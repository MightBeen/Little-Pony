package com.io.portainer.common.check;

import com.io.portainer.common.check.SysTimeCheck;

/**
 * 会被频繁调用的计时器组件
 */
public interface FrequentTickCheck extends SysTimeCheck {
}
