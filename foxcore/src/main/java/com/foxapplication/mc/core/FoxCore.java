package com.foxapplication.mc.core;

import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import lombok.Getter;

public class FoxCore {
    @Getter
    private static Platform platform = Platform.None;
    @Getter
    private static Platform platform1 = Platform.None;
    @Getter
    private static Platform platform2 = Platform.None;

    private static final Log log = LogFactory.get();

    /**
     * 设置平台
     * @param platform 平台
     */
    private static void setPlatform(Platform platform) {
        if (platform != Platform.None) {
            log.info("检测到混合端，当前混合状态为 {} 与 {} 加载器。", platform, FoxCore.platform);
            platform1 = platform;
            platform2 = FoxCore.platform;
            FoxCore.platform = Platform.Mixture;
        } else {
            FoxCore.platform = platform;
        }
    }

    /**
     * 初始化
     * @param platform 平台
     */
    public static void Init(Platform platform) {
        setPlatform(platform);
        if (FoxCore.platform == Platform.Mixture) {
            return;
        }
        log.info("当前加载器为：{} 。", FoxCore.platform);
    }
}
