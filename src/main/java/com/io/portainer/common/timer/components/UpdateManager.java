package com.io.portainer.common.timer.components;

import com.io.core.common.dispacher.SysTaskExecutor;
import com.io.core.common.dispacher.Task;
import com.io.portainer.common.config.FlexibleSetting;
import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.config.SysSetting;
import com.io.portainer.common.timer.Checkable;
import com.io.portainer.common.timer.FixedTickCheck;
import com.io.portainer.common.timer.RegularService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.PriorityQueue;


/**
 * 集中管理数据更新
 */
@Component
@Slf4j
public class UpdateManager implements FixedTickCheck {

    @Autowired
    SysDataCache dataCache;

    @Autowired
    SettingManager settingManager;

    @Autowired
    SysTaskExecutor sysTaskExecutor;

    private List<RegularService<Checkable>> servicesCache;

    @Override
    public boolean execute() {

        updateCacheUnits();
        updateAll();

        return true;
    }



    /**
     * 更新容器中所有项目
     */
    public void updateAll() {
        log.info("=======================缓存数据更新========================");
        // 更新至末尾
        updateByType( this.servicesCache.get(servicesCache.size() - 1).getType());
    }


    private void updateCacheUnits() {
        this.servicesCache = dataCache.getOrderList();
    }

    public void updateByType(Type type) {
        updateByType(type, false);
    }

    public void updateByType(Type type, boolean forceUpdate) {
        // 验证type可用
        if (dataCache.getPriorityQueue(type) == null) {
            throw new IllegalStateException("类型不存在于缓存中: " + type);
        }

        // 按顺序执行更新
        for (RegularService<Checkable> service : this.servicesCache) {
            Type t = service.getType();

            Task task = new Task() {
                @Override
                public void run() {
                    PriorityQueue<Checkable> queue = service.updateAll();
                    boolean res = dataCache.updateCache(type, queue);
                    log.info("执行更新服务：" + type);
                    if (!res) {
                        throw new RuntimeException("数据更新失败：" + type + "\n" + queue);
                    }
                }
            };
            task.setId(service.getOrder());

            if (forceUpdate)
                task.run();
            else
                sysTaskExecutor.regularTask(task);


            if (t.equals(type)) {
                break;
            }
        }
    }

    @Override
    public Long getInterval() {
        return settingManager.getCurrentSetting().getTimerCheck() * 1000L;
    }

    @Override
    public boolean isEnabled() {
        SysSetting currentSetting = settingManager.getCurrentSetting();
        return currentSetting.getAutoCheck();
    }

    @Override
    public void OnException(Throwable throwable) {
        FlexibleSetting flexibleSetting = new FlexibleSetting();
        flexibleSetting.setAutoCheck(false);
        this.settingManager.setSetting(flexibleSetting);
    }
}
