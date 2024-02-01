package com.foxapplication.mc.foxcore.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "foxcorevelocity",
        name = "FoxCoreVelocity",
        version = BuildConstants.VERSION,
        authors = {"KenRouKoro"}
)
public class FoxCoreVelocity {
    public FoxCoreVelocity(){

    }

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }
}
