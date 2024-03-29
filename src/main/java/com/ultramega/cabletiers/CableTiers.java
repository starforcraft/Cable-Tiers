package com.ultramega.cabletiers;

import com.ultramega.cabletiers.config.Config;
import com.ultramega.cabletiers.network.NetworkHandler;
import com.ultramega.cabletiers.registry.ClientEventHandler;
import com.ultramega.cabletiers.registry.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CableTiers.MOD_ID)
public class CableTiers {
    public static final String MOD_ID = "cabletiers";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();

    public CableTiers() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::new);

        RegistryHandler.init();
        ContentType.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
        Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml").toString());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        CableTiers.NETWORK_HANDLER.register();
    }
}
