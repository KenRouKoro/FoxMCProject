package com.foxapplication.mc.foxcore.fabric;

import com.foxapplication.embed.hutool.core.text.UnicodeUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.config.webconfig.WebConfig;
import com.foxapplication.mc.foxcore.fabric.config.FabricMinecraftServerConfig;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;

import java.nio.file.Paths;

public class FoxCoreFabricServer implements DedicatedServerModInitializer {

    private static Log log ;
    @Getter
    private static  DedicatedServer server = null;
    private static FabricMinecraftServerConfig config = null;
    @Override
    public void onInitializeServer() {

        log = LogFactory.get();

        ServerLifecycleEvents.SERVER_STARTED.register(server-> {
            if (server instanceof DedicatedServer dedicatedServer){
                FoxCoreFabricServer.server = dedicatedServer;
                config = new FabricMinecraftServerConfig(Paths.get("server.properties"));
                if (FoxCore.getConfig().isEnabledWebConfig()){
                    WebConfig.addConfig(config);
                }
                server.setMotd(UnicodeUtil.toString(dedicatedServer.getProperties().motd));

            }else {
                log.error("server is not a DedicatedServer");
            }


        });

    }

}
