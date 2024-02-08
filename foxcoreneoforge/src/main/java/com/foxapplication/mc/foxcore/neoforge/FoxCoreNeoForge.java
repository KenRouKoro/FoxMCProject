package com.foxapplication.mc.foxcore.neoforge;

import com.foxapplication.embed.hutool.core.text.UnicodeUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.Platform;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.foxapplication.mc.foxcore.neoforge.config.NeoForgeMinecraftServerConfig;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import net.minecraft.server.dedicated.DedicatedServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import java.nio.file.Paths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FoxCoreNeoForge.MODID)
public class FoxCoreNeoForge {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "foxcoreneoforge";

    private static Log log ;

    @Getter
    private static DedicatedServer server = null;

    private static NeoForgeMinecraftServerConfig config = null;


    public FoxCoreNeoForge(IEventBus modEventBus) {
        // Register the commonSetup method for modloading

        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);
        log = LogFactory.get();
        //初始化
        FoxCore.Init(Platform.Forge);
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        if (event.getServer() instanceof DedicatedServer dedicatedServer){
            server = dedicatedServer;
            config = new NeoForgeMinecraftServerConfig(Paths.get("server.properties"));
            if (FoxCore.getConfig().isEnabledWebConfig()){
                WebConfig.addConfig(config);
            }
            server.setMotd(UnicodeUtil.toString(dedicatedServer.getProperties().motd));

        }else {
            log.error("server is not a DedicatedServer");
        }
    }
    @SubscribeEvent
    public void onServerEnd(ServerStoppingEvent event) {
        FoxCore.onStopping();
        if (!FoxCore.getConfig().isEnabledWebConfig())return;
        log.info("正在关闭FoxCoreWebConfig服务");
        WebConfig.getServer().getRawServer().stop(2);
    }

}
