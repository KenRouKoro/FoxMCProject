package com.foxapplication.mc.core.config.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文件类型接口注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileTypeInterface {
    /**
     * 文件类型
     * @return 文件类型枚举，默认为基本设置
     */
    FileType type() default FileType.TOML;
}
