package com.io.portainer.common.timer.components;

import com.io.core.common.exception.GlobalExceptionHandler;
import com.io.portainer.common.exception.WosSysException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>计时器的异常处理器，用于处理计时器内出现的异常。</p>
 * <p>使用方法同GlobalExceptionHandler。</p>
 * <p>异常处理逻辑：</p>
 * <p>出现异常 -> 异常处理器处理 -> 不能处理 -> 抛出，调用计时器组件的 OnException 方法</p>
 */
@Component
@Slf4j
public class TimerExceptionHandler {
    Map<Class<? extends Throwable>, Method> map = new HashMap<>();

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    HandlerUnit handlerUnit = new HandlerUnit();

    public TimerExceptionHandler(){
        Method[] methods = handlerUnit.getClass().getMethods();

        for (Method m : methods) {
            ExceptionHandler annotation = m.getAnnotation(ExceptionHandler.class);
            if (annotation != null) {
                for (val value : annotation.value()) {
                    map.put(value, m);
                }
            }
        }
    }



    public void HandleException(Throwable throwable) throws Throwable {
        Method method = map.get(throwable.getClass());
        if (method == null) {
            throw throwable;
        }
        try {
            method.invoke(this, throwable);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    static class HandlerUnit{

        @ExceptionHandler(WosSysException.class)
        void WosExceptionHandler(WosSysException e){

        }



    }
}



