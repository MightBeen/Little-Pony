package com.io.portainer.common.timer.components;

import com.io.portainer.common.config.FlexibleSetting;
import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.timer.Checkable;
import com.io.portainer.common.timer.FrequentTickCheck;
import com.io.portainer.common.timer.RegularService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.PriorityQueue;


@Component
@Slf4j
public class CheckManager implements FrequentTickCheck {

    @Autowired
    SysDataCache dataCache;

    @Autowired
    SettingManager settingManager;

    @Autowired
    UpdateManager updateManager;

    private HashMap<Type, CacheUnit> cacheUnits;



    @Override
    public boolean execute() {
        updateCache();
        checkAll();
        return false;
    }

    private void updateCache() {
        this.cacheUnits = dataCache.getCacheUnits();
    }

    private void checkAll(){
        for (Type type : cacheUnits.keySet()) {
            RegularService service = cacheUnits.get(type).getService();

            PriorityQueue<Checkable> queue = dataCache.getPriorityQueue(type);
            Checkable peek = queue.peek();

            if(peek == null) {
                continue;
            }

            LocalDateTime time = peek.getExpired();

            // 如果当前时间大于过期时间
            if(LocalDateTime.now().compareTo(time) >= 0) {
                // 先执行更新
//                PriorityQueue<Checkable> priorityQueue = service.updateAll();
                updateManager.updateByType(type);
                PriorityQueue<Checkable> newQueue = dataCache.getPriorityQueue(type);

                // 递归删除
                this.deleteProcess(service, newQueue);
                // 再次执行更新
                updateManager.updateByType(type, true);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return settingManager.getCurrentSetting().getAutoCheck();
    }

    private void deleteProcess(RegularService service, PriorityQueue<Checkable> queue) {
        Checkable item = queue.peek();
        if (item != null && LocalDateTime.now().compareTo(item.getExpired()) >= 0) {
            service.deleteItem(item);
            queue.remove();
            deleteProcess(service, queue);
        }
    }

    @Override
    public void OnException(Throwable throwable) {
        FlexibleSetting flexibleSetting = new FlexibleSetting();
        flexibleSetting.setAutoCheck(false);
        this.settingManager.setSetting(flexibleSetting);
    }
}
