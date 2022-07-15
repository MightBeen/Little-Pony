package com.io.portainer.common.check.components;

import cn.hutool.core.lang.hash.Hash;
import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.FrequentTickCheck;
import com.io.portainer.common.check.RegularService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


@Component
@Slf4j
public class CheckManager implements FrequentTickCheck {

    // TODO: t
    @Autowired
    SysDataCache dataCache;

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
                return;
            }

            LocalDateTime time = peek.getExpired();

            // 如果当前时间大于过期时间
            if(LocalDateTime.now().compareTo(time) >= 0) {
                // 先执行更新
                // TODO: 可能的异常处理
                PriorityQueue<Checkable> priorityQueue = service.updateAll();

                // 递归删除
                this.deleteProcess(service, priorityQueue);
                // 再次执行更新
                updateManager.updateAll();
            }
        }
    }

    private void deleteProcess(RegularService service, PriorityQueue<Checkable> queue) {
        Checkable item = queue.peek();
        if (item != null && LocalDateTime.now().compareTo(item.getExpired()) >= 0) {
            service.deleteItem(item);
            queue.remove();
            deleteProcess(service, queue);
        }
    }
}
