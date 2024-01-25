package cn.korostudio.ctoml;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 输出注解数据类
 * 用于存储注解的值、位置和数据
 */
@Data
@AllArgsConstructor
public class OutputAnnotationData {
    /**
     * 注解值
     */
    String value;
    /**
     * 注解位置
     */
    Location at;
    /**
     * 注解数据
     */
    Object data;
}
