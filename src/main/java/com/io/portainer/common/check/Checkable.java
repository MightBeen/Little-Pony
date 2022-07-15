package com.io.portainer.common.check;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


public interface Checkable extends Comparable<Checkable>{
    Long getId();
    LocalDateTime getUpdated();
    LocalDateTime getExpired();

    @Override
    default int compareTo(Checkable o) {
        LocalDateTime expired = getExpired();
//        if (expired == null)
//            return 1;

        if (o == null)
            throw new IllegalArgumentException("参数不能为null");

//        if ( o.getExpired() == null){
//
//        }
        Long second = expired.toEpochSecond(ZoneOffset.of("+8"));
        return second.compareTo(o.getExpired().toEpochSecond(ZoneOffset.of("+8")));
    }
}
