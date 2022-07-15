package com.io.portainer.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.io.portainer.common.annotation.PtrMapper;
import com.io.portainer.data.entity.ptr.PtrEndpoint;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 用于解析来自portainer的json
 */
public class PtrJsonParser<T> {

    private Class<T> type;

    PtrSerializer<T> ptrSerializer;

    public PtrJsonParser(Class<T> type) {
        this.type = type;
        this.ptrSerializer = new PtrSerializer<>(type);
    }

    /**
     * json 反序列化为实体类数组
     * @param jsonArray 数组格式json
     * @return 对象T
     * @throws JsonProcessingException
     */
    public List<T> parseJsonArray(String jsonArray) throws JsonProcessingException {
        ObjectMapper objMapper = getObjectMapper();
        List<T> list = objMapper.readValue(jsonArray, objMapper.getTypeFactory().constructCollectionType(List.class, type));

        return list;
    }

    public T parseJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();

        return (T) objectMapper.readValue(json, type);
    }

    public List<Field> getUpdatableFields() {
        return getNotMarkedFieldsProcess(this.type, new ArrayList<>());
    }

    private List<Field> getNotMarkedFieldsProcess(Class<?> type, List<Field> notMarkedFields) {
        if (type == Object.class)
            return notMarkedFields;

        for (Field f : type.getDeclaredFields()) {
            PtrMapper ptrMapper = f.getAnnotation(PtrMapper.class);

            // 跳过常量和静态字段
            if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            if (ptrMapper != null) {
                if (ptrMapper.persisted()) {
                    notMarkedFields.add(f);
                    continue;
                } else if (type == this.type || ptrMapper.inherited())
                    continue;
            }
            notMarkedFields.add(f);
        }
        return getNotMarkedFieldsProcess(type.getSuperclass(), notMarkedFields);
    }

    /**
     * 获取配置好的objMapper
     */
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("PtrMapperDeserializer");
        module.addDeserializer(type, ptrSerializer);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    /**
     * 自定义的序列化器
     */
    private class PtrSerializer<K> extends StdDeserializer<K> {


        protected PtrSerializer(Class<K> src) {
            super(src);
        }

        /**
         * 提取字段
         */
        private HashMap<String, Field> getMapFieldByAnnotation() {
            HashMap<String, Field> mapFields = new HashMap<>();
            // 获取当前类的所有字段，并将字段名首字母大写
            getMapFieldProcess(_valueClass, mapFields);

            return mapFields;
        }

        /**
         * 首字母大写
         *
         * @param fieldName 需要转化的字符串
         */
        private String headUpperCase(String fieldName) {
            char[] chars = fieldName.toCharArray();
            chars[0] = toUpperCase(chars[0]);
            return String.valueOf(chars);
        }


        /**
         * 字符转成大写
         *
         * @param c 需要转化的字符
         */
        private char toUpperCase(char c) {
            if (97 <= c && c <= 122) {
                c ^= 32;
            }
            return c;
        }

        /**
         * 获取字段进程（递归调用，直到父类部位Object.class）
         */
        private HashMap<String, Field> getMapFieldProcess(Class<?> type, HashMap<String, Field> mapFields) {
            if (type == Object.class)
                return mapFields;

            for (Field f : type.getDeclaredFields()) {
                PtrMapper ptrMapper = f.getAnnotation(PtrMapper.class);

                // 跳过常量和静态字段
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                if (ptrMapper != null && (type == _valueClass || ptrMapper.inherited())) {
                    String fieldName = "null".equals(ptrMapper.ptrAlias()) ? f.getName() : ptrMapper.ptrAlias();
                    fieldName = headUpperCase(fieldName);
//                    System.out.println(fieldName);
                    mapFields.put(fieldName, f);
                }
            }
            return getMapFieldProcess(type.getSuperclass(), mapFields);
        }

        private List<Long> myListParser(JsonNode jsonNode) {
            if (this._valueClass == PtrEndpoint.class) {
                List<Long> res = new ArrayList<>();
                Iterator<String> iterator = jsonNode.fieldNames();
                while (iterator.hasNext()){
                    long l = Long.parseLong(iterator.next());
                    res.add(l);
                }
                return res;
            }
            return null;
        }

        @Override
        public K deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            K res = null;
            try {
                res = (K) type.newInstance();

                // 将映射字段提取，并用哈希表储存
                HashMap<String, Field> mapFields = getMapFieldByAnnotation();

                ObjectCodec objectCodec = jsonParser.getCodec();

                JsonNode jsonNode = objectCodec.readTree(jsonParser);

                for (String fieldName : mapFields.keySet()) {
                    if (jsonNode.has(fieldName)) {
                        Field field = mapFields.get(fieldName);
                        field.setAccessible(true);
                        JsonNode jsonValue = jsonNode.get(fieldName);

                        if (field.getType() == Integer.class) {
                            field.set(res, jsonValue.asInt());
                        } else if (field.getType() == Long.class) {
                            field.set(res, jsonValue.asLong());
                        } else if (field.getType() == String.class) {
                            field.set(res, jsonValue.asText());
                        } else if (field.getType() == List.class) {
                            List<?> list = myListParser(jsonValue);
                            field.set(res, list);
                        }
                        else {
                            throw new IllegalArgumentException("尚未录入的类型" + jsonValue);
                        }

                        field.setAccessible(false);
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                // TODO ：异常处理
                e.printStackTrace();
            }
            return res;
        }
    }
}
