package com.foxapplication.mc.core.config;

import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;
import com.foxapplication.mc.core.config.interfaces.FileType;
import com.foxapplication.mc.core.config.interfaces.FileTypeInterface;
import lombok.Data;

@FileTypeInterface(type = FileType.TOML)
@Data
public class FoxCoreConfig {
    @FieldAnnotation(name = "Web功能开关" ,value = "是否启用FoxWebConfig配置系统")
    boolean EnabledWebConfig = true;
    @FieldAnnotation(name = "Web端口" ,value = "FoxWebConfig配置系统使用的web端口")
    int WebPort = 8620;

}
