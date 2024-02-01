package com.foxapplication.mc.foxcore.waterfall;

import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.Platform;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import net.md_5.bungee.api.plugin.Plugin;

public final class FoxCoreWaterfall extends Plugin {

    public static Log log;
    @Override
    public void onEnable() {
        // Plugin startup logic
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);

        FoxCore.Init(Platform.BungeeCord);
        log = LogFactory.get();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (!FoxCore.getConfig().isEnabledWebConfig())return;
        log.info("正在关闭FoxCoreWebConfig服务");
        WebConfig.getServer().getRawServer().stop(2);
    }
}
