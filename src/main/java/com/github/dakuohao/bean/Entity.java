package com.github.dakuohao.bean;

import java.util.LinkedHashMap;

/**
 * 封装统一的实体对象
 * 对应JavaBean
 *
 * @author Peng 1029538990@qq.com
 */
public class Entity extends LinkedHashMap<String, Object> {


    /**
     * 获得特定类型值
     *
     * @param key 字段
     * @param <T> 返回泛型
     * @return 字段值T
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) super.get(key);
    }

    /**
     * 获得特定类型值
     *
     * @param <T>          值类型
     * @param key          字段名
     * @param defaultValue 默认值
     * @return 字段值T
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        final Object result = get(key);
        return (T) (result != null ? result : defaultValue);
    }

    /**
     * 设置属性值
     *
     * @param key   字段
     * @param value 字段值
     * @return 字段值
     */
    public Object set(String key, Object value) {
        return put(key, value);
    }


}
