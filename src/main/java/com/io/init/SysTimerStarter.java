package com.io.init;

import com.io.core.common.exception.GlobalExceptionHandler;
import com.io.portainer.common.timer.FixedTickCheck;
import com.io.portainer.common.timer.FrequentTickCheck;
import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.timer.SysTimeCheck;
import com.io.portainer.common.timer.components.TimerExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用于启用系统计时器服务，只会启动一次。
 * 通过ioc容器注册，将所有实现SysTimer接口的类作为组件
 */
@Slf4j
public class SysTimerStarter{

    private final ApplicationContext appContext;

    private static SysTimerStarter instance;

    private final TimerExceptionHandler exceptionHandler;

    private final SettingManager setting;

    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 15, 1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 使用计时服务的组件，通过ioc容器注册
     */
    Map<String, ComponentsUnit> tickComponents = new HashMap<String, ComponentsUnit>();


    /**
     * 私有化构造器
     */
    private SysTimerStarter(ApplicationContext appContext, SettingManager setting){
        this.appContext = appContext;
        this.setting = setting;
        this.exceptionHandler = appContext.getBean(TimerExceptionHandler.class);
    }


    private void registrar(){
        Map<String, SysTimeCheck> beans = appContext.getBeansOfType(SysTimeCheck.class);
        for (String bean : beans.keySet()) {
            SysTimeCheck sysTimeCheck = beans.get(bean);
            this.tickComponents.put(bean, new ComponentsUnit(sysTimeCheck, 0L));
        }

        // 日志打印
        log.info("======================= SysTimer注册 =================================");
        for (String key : tickComponents.keySet()) {
            log.info("计时器组件已注册：" + key);
        }
    };

    public static void start(ApplicationContext context,  SettingManager setting){
        // 如果尚未存在实例
        if (instance == null){
            instance = new SysTimerStarter(context, setting);
            instance.registrar();
            Thread process = new Thread(new Runnable() {
                @Override
                public void run() {
                    instance.process();
                }
            });

            process.start();

            System.out.println("start !");
        }
        else{
            throw new RuntimeException("Cannot register a timer again");
        }

    }


    private void process(){
        while (true) {
            for (String key : tickComponents.keySet()) {
                ComponentsUnit componentsUnit = tickComponents.get(key);
                if (! componentsUnit.getTickCheck().isEnabled())
                    continue;

                if(System.currentTimeMillis() - componentsUnit.getLastUpdated() > componentsUnit.getTickCheck().getInterval()){
                    if (!(componentsUnit.getTickCheck() instanceof FrequentTickCheck)){
                        log.info("======================= 定期执行 ================================");
                        log.info("定期执行：" + key);
                    }

                    // TODO: 2022/9/23 改用调度器
                    threadPool.execute(() -> {

                        try {
                            componentsUnit.getTickCheck().execute();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            try {
                                this.exceptionHandler.HandleException(e);
                            } catch (Throwable ex) {
                                log.error(ex.getMessage(), ex);
                                componentsUnit.getTickCheck().OnException(ex);
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    componentsUnit.setLastUpdated(System.currentTimeMillis());
                }
            }

        }
    }

}
@Data
@AllArgsConstructor
class ComponentsUnit{
    SysTimeCheck tickCheck;
    Long lastUpdated;
}