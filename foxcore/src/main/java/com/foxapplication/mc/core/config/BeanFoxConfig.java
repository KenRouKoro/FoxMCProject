package com.foxapplication.mc.core.config;

import com.foxapplication.embed.hutool.core.bean.BeanDesc;
import com.foxapplication.embed.hutool.core.bean.BeanUtil;
import com.foxapplication.embed.hutool.core.bean.DynaBean;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;
import com.foxapplication.mc.core.config.interfaces.FieldAnnotationData;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BeanFoxConfig类是FoxConfig接口的实现类，用于配置Bean对象的相关信息。
 * @see FoxConfig
 */
public class BeanFoxConfig implements FoxConfig {
    @Getter
    private final DynaBean config; // 配置对象
    @Getter
    private final BeanDesc desc; // Bean对象的描述信息
    @Setter
    @Getter
    private String name;
    private final List<FieldChangedListener> fieldChangedListeners = new CopyOnWriteArrayList<>();

    /**
     * 构造方法，通过传入的bean对象创建BeanFoxConfig实例。
     * @param bean 传入的bean对象
     */
    public BeanFoxConfig(Object bean) {
        config = DynaBean.create(bean);
        desc = BeanUtil.getBeanDesc(config.getBeanClass());
        name = desc.getSimpleName();
    }

    /**
     * 构造方法，通过传入的类类型创建BeanFoxConfig实例。
     * @param bean 传入的类类型
     */
    public BeanFoxConfig(Class<?> bean) {
        config = DynaBean.create(bean);
        desc = BeanUtil.getBeanDesc(config.getBeanClass());
        name = desc.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getList() {
        return desc.getPropMap(false).keySet().stream().toList();
    }

    private void addToWebConfig(){
        if (FoxCore.getConfig().isEnabledWebConfig()){
            WebConfig.addConfig(this);
        }
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
        FieldChangedEvent event = new FieldChangedEvent(str, value);
        fieldChangedListeners.forEach(listener -> listener.onFieldChanged(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldAnnotationData getAnnotation(String name) {
        // 获取属性字段
        Field field = desc.getProp(name).getField();

        return FieldAnnotationData.getByAnnotation(field.getAnnotation(FieldAnnotation.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String configName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigName(String configName) {
        setName(configName);
    }

    /**
     * 添加字段变更监听器。
     * @param runnable 字段变更监听器
     * @return 添加后的字段变更监听器
     */
    public FieldChangedListener addFieldChangedListener(FieldChangedListener runnable){
        fieldChangedListeners.add(runnable);
        return runnable;
    }

    /**
     * 移除字段变更监听器。
     * @param runnable 字段变更监听器
     * @return 移除后的字段变更监听器
     */
    public FieldChangedListener removeFieldChangedListener(FieldChangedListener runnable){
        fieldChangedListeners.remove(runnable);
        return runnable;
    }

    /**
     * FieldChangedEvent类是字段变更事件类，用于存储字段变更的相关信息。
     */
    @Data
    @AllArgsConstructor
    public static class FieldChangedEvent{
        private String fieldName; // 字段名称
        private Object changeValue; // 变更后的值
    }

    /**
     * FieldChangedListener接口是字段变更监听器接口，用于监听字段变更事件。
     */
    public interface FieldChangedListener{
        /**
         * 字段变更事件处理方法。
         * @param event 字段变更事件
         */
        void onFieldChanged(FieldChangedEvent event);
    }

}

