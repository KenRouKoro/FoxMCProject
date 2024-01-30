package cn.korostudio.ctoml;

import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputAnnotation {
    String value() default "";
    Location at() default Location.Top;

}
