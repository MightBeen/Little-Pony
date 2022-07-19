package com.io.portainer.common.check.components;

import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.FixedTickCheck;
import com.io.portainer.common.check.RegularService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
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

    private List<RegularService> servicesCache;

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
        for (RegularService service : servicesCache) {
            Type type = service.getType();
            PriorityQueue<Checkable> queue = service.updateAll();
            boolean res = dataCache.updateCache(type, queue);
            log.info("执行更新服务：" + type);
            if (!res) {
                throw new RuntimeException("数据更新失败：" + type + "\n" + queue);
            }
        }
    }


    private void updateCacheUnits() {
        this.servicesCache = dataCache.getOrderList();
    }
}
