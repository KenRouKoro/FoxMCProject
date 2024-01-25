package com.foxapplication.mc.core.config.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解用于标记字段或类，用于给字段或类添加自定义的属性和值
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FieldAnnotation {
    /**
     * 属性值为字符串类型，用于给字段或类添加一个字符串类型的属性和值
     */
    String value();
}
