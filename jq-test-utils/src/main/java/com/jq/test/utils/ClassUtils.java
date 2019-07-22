package com.jq.test.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtils {

    /**
     * 赋值
     *
     * @param name       字段名称
     * @param fieldValue 字段的值
     * @param request    所要赋值的对象
     * @param <X>        字段类型
     * @param <Y>        对象的类型
     * @throws NoSuchMethodException     异常
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException    异常
     */
    public static <X, Y> void setValue(String name, X fieldValue, Y request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = buildSetMethod(request.getClass(), name, fieldValue.getClass());
        method.invoke(request, fieldValue);
    }

    /**
     * 赋值
     *
     * @param field      字段
     * @param fieldValue 字段的值
     * @param request    所要赋值的对象
     * @param <X>        字段的类型
     * @param <Y>        对象的类型
     * @throws NoSuchMethodException     异常
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException    异常
     */
    public static <X, Y> void setValue(Field field, X fieldValue, Y request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = buildSetMethod(request.getClass(), field);
        method.invoke(request, fieldValue);
    }

    private static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else {
            String result = String.valueOf(Character.toUpperCase(s.charAt(0)));
            return result + s.substring(1);
        }
    }

    private static Method buildSetMethod(Class<?> aClass, Field field) throws NoSuchMethodException {
        String getter = "set" + toUpperCaseFirstOne(field.getName());
        return aClass.getMethod(getter, field.getType());
    }

    private static Method buildSetMethod(Class<?> aClass, String actualFieldName, Class<?> type) throws NoSuchMethodException {
        String getter = "set" + toUpperCaseFirstOne(actualFieldName);
        return aClass.getMethod(getter, type);
    }

    /**
     * @param aClass          类
     * @param actualFieldName 字段名称
     * @return get方法
     * @throws NoSuchMethodException 异常
     */
    public static Method buildGetMethod(Class<?> aClass, String actualFieldName) throws NoSuchMethodException {
        String getter = "get" + toUpperCaseFirstOne(actualFieldName);
        return aClass.getMethod(getter);
    }

    /**
     * @param clz  对象
     * @param name 字段名称
     * @param <X>  对象的类型
     * @return 对象对应字段的值
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException    异常
     * @throws NoSuchMethodException     异常
     */
    public static <X> Object getValue(X clz, String name) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = buildGetMethod(clz.getClass(), name);
        return method.invoke(clz);
    }
}
