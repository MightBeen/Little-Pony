package com.io.portainer.common.utils;

import com.io.core.common.wrapper.ConstValue;
import com.io.portainer.data.entity.ptr.PtrBaseEntity;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.data.entity.ptr.ResourceType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    public static List<Long> entityToIdList(List<? extends PtrBaseEntity> entities) {
        List<Long> ids = new ArrayList<>();
        entities.forEach(i -> ids.add(i.getId()));
        return ids;
    }

    /**
     * // 将新数据打平
     */
    public static List<PtrUserEndpoint> flatEndpoints(List<PtrEndpoint> endpoints) {
        List<PtrUserEndpoint> ueList = new ArrayList<>();

        endpoints.forEach(e -> {
            e.getUserIds().forEach(userId ->{
                PtrUserEndpoint data = new PtrUserEndpoint();
                data.setEndpointId(e.getId());
                data.setUserId(userId);
                ueList.add(data);
            });
        });
        return ueList;
    }

    public static String portainerFormatWrapper(List<Long> accessId) {
        String prefix = "{\"UserAccessPolicies\": {";
        StringBuilder json = new StringBuilder(prefix);

        if (accessId.size() == 0) {
            return json.append("}}").toString();
        }

        for (Long id : accessId) {
            json.append("\"").append(id).append("\": {\"RoleId\": 0},");
        }
        char[] chars = json.toString().toCharArray();
        chars[chars.length - 1] = '}';

        return String.valueOf(chars) + "}";
    }

    // TODO: 2022/7/18 将此方法一起加到工厂中
    public static int getCapacity(Integer resourceType) {
        int res;
        if (ConstValue.SINGLE_RESOURCE.equals(resourceType)) {
            res = ConstValue.SINGLE_RESOURCE_CAPACITY;
        }
        else if (ConstValue.GROUP_RESOURCE.equals(resourceType)) {
            throw new IllegalArgumentException("还没做");
        }
        else
            throw new IllegalArgumentException("Invalid resource type: " + resourceType);
        return res;
    }


    public static List<Integer> resourceTypeCodes(){
        List<Integer> result = new ArrayList<Integer>();
        for (ResourceType type : ResourceType.values()) {
            result.add(type.code);
        }
        return result;
    }

    /**
     * 用于注入portainer实体类字段。方法内未作校验，使用时需保证item 和 value 不为空且为同一实体类
     * @param fields 需要替换的字段
     * @param item 被替换对象
     * @param value 值提供对象
     */
    public static void fieldInjection(List<Field> fields, Object item, Object value){
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(item, field.get(value));
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
    }
}
