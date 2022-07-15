package com.io.portainer.common.check;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.PriorityQueue;

public interface RegularService<T extends Checkable> {
    /**
     * 定期执行的行为
     * @return 更新后的数据
     */
    PriorityQueue<Checkable> updateAll();

    default Type getType(){
        Type[] types = this.getClass().getGenericInterfaces();
        Type type = null;
        for (Type t : types){
            if (t.getTypeName().matches(RegularService.class.getTypeName() + ".*")){
                type = t;
                break;
            }
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        }
        throw new RuntimeException();
    };

    void deleteItem(Checkable item);
}
