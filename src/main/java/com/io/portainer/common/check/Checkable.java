package com.io.portainer.common.check;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 表明该类为定期检查的对象
 */
public interface Checkable extends Comparable<Checkable>{
    Long getId();
    LocalDateTime getUpdated();

    /**
     * 获取过期时间，如果过期时间小于当前时间，则执行删除
     * @return
     */
    LocalDateTime getExpired();

    @Override
    default int compareTo(Checkable o) {
        LocalDateTime expired = getExpired();

        if (o == null)
            throw new IllegalArgumentException("参数不能为null");


        Long second = expired.toEpochSecond(ZoneOffset.of("+8"));
        return second.compareTo(o.getExpired().toEpochSecond(ZoneOffset.of("+8")));
    }
}
