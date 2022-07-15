package com.io.portainer.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表明该字段是portainer的映射
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PtrMapper {
    /**
     * 首字母会自动转换成大写！如果为null，则取字段名
     */
    String ptrAlias() default "null";

    /**
     * 是否会被子类继承
     */
    boolean inherited() default true;

    /**
     * 数据库中对应字段是否会持久化，而不会随portainer更新。
     */
    boolean persisted() default false;
}
