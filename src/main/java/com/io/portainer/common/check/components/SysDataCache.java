package com.io.portainer.common.check.components;


import com.io.portainer.common.check.Checkable;
import com.io.portainer.common.check.RegularService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

@Component
@Slf4j
public class SysDataCache {

    @Autowired
    ApplicationContext beans;

    private boolean initialized = false;

    private HashMap<Type, CacheUnit> unitMaps = new HashMap<>();

    private List<RegularService> orderList = new ArrayList<>();


    /**
     * 注册缓存容器
     *
     * @param type    实现checkable的实体类型
     * @param service 对应service类
     * @param queue   对应队列
     */
    private void registeredSysCache(Type type, RegularService<Checkable> service, PriorityQueue<Checkable> queue) {
        if (queue == null) {
            throw new IllegalArgumentException("queue must not be null");
        }

        if (type == null)
            throw new IllegalArgumentException("clazz must not be null");

        if (unitMaps.containsKey(type))
            throw new RuntimeException("实体类型类型重复注册：" + type);

        unitMaps.put(type, new CacheUnit(type, service, queue));
    }


    /**
     * 通过类型获取对应容器，仅同包类可使用
     */
    PriorityQueue<Checkable> getPriorityQueue(Type type) {
        return unitMaps.get(type).queue;
    }


    /**
     * 指定更新某一实体类型的容器，仅同包下可用
     */
    boolean updateCache(Type type, PriorityQueue<Checkable> data) {
        CacheUnit cacheUnit = unitMaps.get(type);
        if (cacheUnit == null) {
            log.error("\"" + type + "\" 对应缓存单元为空");
            return false;
        }
        cacheUnit.queue = data;
        return true;
    }


    /**
     * 获取orderList，仅同包下可用
     *
     * @return
     */
    List<RegularService> getOrderList() {
        return this.orderList;
    }


    /**
     * 初始化时会自动配置容器
     */
    public void init() {
        if (this.initialized) {
            log.warn("CacheUnit is already initialized");
            return;
        }
        this.initialized = true;


        // 初始化缓存单元
        this.unitMaps = new HashMap<>();
        Map<String, RegularService> map = beans.getBeansOfType(RegularService.class);

        for (String bean : map.keySet()) {
            orderList.add(map.get(bean));
        }
        // 按order排序
        orderList.sort((o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));

        // 按顺序执行更新
        orderList.forEach(service -> {
            Type type = service.getType();
            PriorityQueue<Checkable> queue = service.updateAll();
            this.registeredSysCache(type, service, queue);
            log.info("初始化完成：" + type);
        });

    }

    /**
     * 获取所有缓存数据，仅能由manager调用
     */
    HashMap<Type, CacheUnit> getCacheUnits() {
        return this.unitMaps;
    }

}

/**
 * 缓存单元。
 */
@Data
class CacheUnit {
    Type type;

    PriorityQueue<Checkable> queue;

    RegularService<Checkable> service;

    CacheUnit(Type type, RegularService<Checkable> service, PriorityQueue<Checkable> queue) {
        this.type = type;
        this.service = service;
        this.queue = queue;
    }
}