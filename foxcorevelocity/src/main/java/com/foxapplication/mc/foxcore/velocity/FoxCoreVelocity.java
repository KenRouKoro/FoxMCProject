package com.foxapplication.mc.foxcore.velocity;

import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.Platform;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ListenerCloseEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "foxcorevelocity",
        name = "FoxCoreVelocity",
        version = BuildConstants.VERSION,
        authors = {"KenRouKoro"}
)
public class FoxCoreVelocity {

    private final ProxyServer server;
    private final Logger logger;
    @Inject
    public FoxCoreVelocity(ProxyServer server, Logger logger){
        this.server = server;
        this.logger = logger;
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);

        FoxCore.Init(Platform.Velocity);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }
    @Subscribe
    public void onClose(ProxyShutdownEvent event){
        if (!FoxCore.getConfig().isEnabledWebConfig())return;
        logger.info("正在关闭FoxCoreWebConfig服务");
        WebConfig.getServer().getRawServer().stop(2);
    }
}
