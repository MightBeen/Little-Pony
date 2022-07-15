package com.io.portainer.common.utils;

import com.io.portainer.data.entity.ptr.PtrBaseEntity;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    public static List<Long> entityToIdList(List<? extends PtrBaseEntity> entities) {
        List<Long> ids = new ArrayList<>();
        entities.forEach(i -> ids.add(i.getId()));
        return ids;
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
}
