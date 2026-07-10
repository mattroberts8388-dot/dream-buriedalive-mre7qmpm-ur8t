package com.buriedalive;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuriedAliveMod implements ModInitializer {
    public static final String MOD_ID = "buriedalive";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Buried Alive] Initializing...");
        BuriedAliveEvents.register();
        LOGGER.info("[Buried Alive] Ready. Prepare to be buried!");
    }
}