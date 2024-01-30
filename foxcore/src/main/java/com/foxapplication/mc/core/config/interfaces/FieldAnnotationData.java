package com.foxapplication.mc.core.config.interfaces;

import cn.korostudio.ctoml.OutputAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段注释的数据类。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldAnnotationData {
    /**
     * 注解。
     */
    private String annotation = "";

    /**
     * 字段别名。
     */
    private String name = "";

    /**
     * 根据注解获取字段注释数据。
     *
     * @param annotation 字段注解
     * @return 字段注释数据
     */
    public static FieldAnnotationData getByAnnotation(FieldAnnotation annotation) {
        if (annotation == null) {
            return new FieldAnnotationData();
        }
        return new FieldAnnotationData(annotation.value(), annotation.name());
    }
}
