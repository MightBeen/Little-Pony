package com.io.init;

import com.io.portainer.common.check.FixedTickCheck;
import com.io.portainer.common.check.FrequentTickCheck;
import com.io.portainer.common.config.SettingManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 用于启用系统计时器服务，只会启动一次。
 * 通过ioc容器注册，将所有实现SysTimer接口的类作为组件
 */
@Slf4j
public class SysTimerStarter{

    private final ApplicationContext appContext;

    private static SysTimerStarter instance;

    private final SettingManager setting;

    /**
     * 执行单元
     */
    private final FixedUnit fixedUnit;

    private final FrequentUnit frequentUnit;


    /**
     * 使用计时服务的组件，通过ioc容器注册
     */
    Map<String, FrequentTickCheck> frequentTickComponents;

    Map<String, FixedTickCheck> fixedTickComponents;


    /**
     * 私有化构造器
     */
    private SysTimerStarter(ApplicationContext appContext, SettingManager setting){
        this.appContext = appContext;
        this.setting = setting;
        this.frequentUnit = new FrequentUnit();
        this.fixedUnit = new FixedUnit();
    }


    private void registrar(){
        frequentTickComponents = appContext.getBeansOfType(FrequentTickCheck.class);
        fixedTickComponents = appContext.getBeansOfType(FixedTickCheck.class);

        // 日志打印
        log.info("======================= SysTimer注册 =================================");
        for (String key : frequentTickComponents.keySet()) {
            log.info("frequentTick组件已注册：" + key);
        }
        for (String key : fixedTickComponents.keySet()) {
            log.info("fixedTick组件已注册：" + key);
        }
    };

    public static void start(ApplicationContext context,  SettingManager setting){
        // 如果尚未存在实例
        if (instance == null){
            instance = new SysTimerStarter(context, setting);
            instance.registrar();
            Thread fixedThread = new Thread(instance.fixedUnit);
            Thread frequentThread = new Thread(instance.frequentUnit);

            fixedThread.start();
            frequentThread.start();

            System.out.println("start !");
        }
        else{
            throw new RuntimeException("Cannot register a timer again");
        }

    }

    private class FrequentUnit  implements Runnable{
        @Override
        public void run() {
            while (true) {
//                System.out.println("周期开始");
                    // 执行组件中需要定期调用的方法
//                    System.out.println("======================= 定期执行 ================================");
                long snap01 = System.currentTimeMillis();

                while (true) {
                    long snap02 = System.currentTimeMillis();

                    // 设置时间间隔
                    if (snap02 - snap01 >= 1000){
//                        log.info("Snap");
                        for (String key : frequentTickComponents.keySet()) {
                            frequentTickComponents.get(key).execute();
//                            log.info("定期执行：" + key);
                        }
                        break;
                    }
                }
            }
        }
    }

    private class FixedUnit  implements Runnable{
        @Override
        public void run() {
            while (true) {
                try {
//                System.out.println("周期开始");
                    // 执行组件中需要定期调用的方法
                    log.info("======================= 定期执行 ================================");
                    for (String key : fixedTickComponents.keySet()) {
                        log.info("定期执行：" + key);
                        boolean res = fixedTickComponents.get(key).execute();

                        if (res == false){
                            // TODO: 异常日志输出
                        }
                    }

                    Thread.sleep(setting.getCurrentSetting().getTimerCheck() * 1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
