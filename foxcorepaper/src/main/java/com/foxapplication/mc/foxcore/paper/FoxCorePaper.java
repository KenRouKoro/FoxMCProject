package com.foxapplication.mc.foxcore.paper;

import com.foxapplication.embed.hutool.core.text.UnicodeUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.Platform;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.foxapplication.mc.foxcore.paper.config.PaperMinecraftServerConfig;
import lombok.Getter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public final class FoxCorePaper extends JavaPlugin implements Listener {

    private static Log log ;
    @Getter
    private static DedicatedServer dedicatedServer;

    private static PaperMinecraftServerConfig config;

    private static CraftServer craftServer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);
        log = LogFactory.get();
        //初始化
        FoxCore.Init(Platform.Paper);
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public @NotNull ComponentLogger getComponentLogger() {
        return super.getComponentLogger();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (!FoxCore.getConfig().isEnabledWebConfig())return;
        log.info("正在关闭FoxCoreWebConfig服务");
        WebConfig.getServer().getRawServer().stop(2);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerStart(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD)return;

        if (getServer() instanceof CraftServer craftServer){
            FoxCorePaper.craftServer = craftServer;
            dedicatedServer = craftServer.getServer();
        }else {
            log.error("服务器实例获取失败。");
            return;
        };

        config = new PaperMinecraftServerConfig(Paths.get("server.properties"));
        if (FoxCore.getConfig().isEnabledWebConfig()){
            WebConfig.addConfig(config);
        }
        dedicatedServer.setMotd(UnicodeUtil.toString(dedicatedServer.getProperties().motd));
    }
}
