package com.foxapplication.mc.core.config.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记字段或类，用于给字段或类添加注释
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FieldAnnotation {
    /**
     * 属性值为字符串类型，字段注释
     */
    String value();
    /**
     * 字段别名，默认值为""，在空的情况下视作与value相同
     *
     * @return 默认值为""
     */
    String name() default "";

}
