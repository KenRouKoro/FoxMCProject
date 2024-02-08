package com.foxapplication.mc.foxcore.fabric;

import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.Platform;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.foxapplication.mc.foxcore.fabric.config.FabricMinecraftServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FoxCoreFabric implements ModInitializer {
    private static Log log ;

    // Fabric服务器配置
    private static FabricMinecraftServerConfig config ;

    @Override
    public void onInitialize() {
        // 设置日志门面
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);
        log = LogFactory.get();
        // 初始化
        FoxCore.Init(Platform.Fabric);

        // 服务器生命周期事件
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            // 如果启用了WebConfig
            if (!FoxCore.getConfig().isEnabledWebConfig()) return;
            log.info("正在关闭FoxCoreWebConfig服务");
            WebConfig.getServer().getRawServer().stop(2);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            FoxCore.onStopping();
        });
    }
}
