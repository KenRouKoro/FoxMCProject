package com.foxapplication.mc.core.config;

import com.foxapplication.embed.hutool.core.bean.BeanDesc;
import com.foxapplication.embed.hutool.core.bean.BeanUtil;
import com.foxapplication.embed.hutool.core.bean.DynaBean;
import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * BeanFoxConfig类是FoxConfig接口的实现类，用于配置Bean对象的相关信息。
 * @see FoxConfig
 */
public class BeanFoxConfig implements FoxConfig {
    private final DynaBean config; // 配置对象
    private final BeanDesc desc; // Bean对象的描述信息
    @Setter
    @Getter
    private String name;
    /**
     * 构造方法，通过传入的bean对象创建BeanFoxConfig实例。
     * @param bean 传入的bean对象
     */
    public BeanFoxConfig(Object bean) {
        config = new DynaBean(bean);
        desc = BeanUtil.getBeanDesc(config.getBeanClass());
        name = desc.getSimpleName();
    }

    /**
     * 构造方法，通过传入的类类型创建BeanFoxConfig实例。
     * @param bean 传入的类类型
     */
    public BeanFoxConfig(Class<?> bean) {
        config = new DynaBean(bean);
        desc = BeanUtil.getBeanDesc(config.getBeanClass());
        name = desc.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getList() {
        return desc.getPropMap(true).keySet().stream().toList();
    }

    /**
     * 获取配置对象的bean对象。
     * @return 配置对象的bean对象
     */
    public Object getBean() {
        return config.getBean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(String str) {
        return config.get(str);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String str, Object value) {
        config.set(str, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAnnotation(String name) {
    // 获取属性字段
    Field field = desc.getProp(name).getField();

    // 检查FieldAnnotation注解是否存在
    FieldAnnotation annotation = field.getAnnotation(FieldAnnotation.class);

    // 如果注解存在，则返回其value值；否则返回null
    return annotation != null ? annotation.value() : null;
    }

    @Override
    public String configName() {
        return name;
    }

    @Override
    public void setConfigName(String configName) {
        setName(configName);
    }

}
