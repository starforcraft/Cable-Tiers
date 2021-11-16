package com.YTrollman.CableTiers;

import com.YTrollman.CableTiers.config.Config;
import com.YTrollman.CableTiers.init.ClientEventHandler;
import com.YTrollman.CableTiers.registry.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CableTiers.MOD_ID)
public class CableTiers {

    public static final String MOD_ID = "cabletiers";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CableTiers() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::new);
        RegistryHandler.init();
        ContentType.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.client_config);
        Config.loadConfig(Config.client_config, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());
    }
}
