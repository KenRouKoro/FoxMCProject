package com.foxapplication.mc.core.config;

import com.foxapplication.mc.core.config.interfaces.FieldAnnotationData;

import java.util.List;

/**
 * 配置接口
 */
public interface FoxConfig {
    /**
     * 获取配置名称列表
     * @return 列表
     */
    List<String> getList();

    /**
     * 根据名称获取值
     * @param name 名称
     * @return 值
     */
    Object getValue(String name);

    /**
     * 设置名称对应的值
     * @param name 名称
     * @param value 值
     */
    void setValue(String name, Object value);

    /**
     * 根据名称获取注释
     * @param name 名称
     * @return 注释
     */
    FieldAnnotationData getAnnotation(String name);
    /**
     * 获取配置名称
     * @return 名称
     */
    String configName();
    /**
     * 设置配置名称
     *
     * @param configName 配置名称
     */
    void setConfigName(String configName);

}
