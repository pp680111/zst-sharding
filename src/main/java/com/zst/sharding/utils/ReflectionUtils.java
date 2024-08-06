package com.zst.sharding.utils;

import java.lang.reflect.Field;

public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            // TODO 待完善异常处理
            throw new RuntimeException(e);
        }
    }
}
