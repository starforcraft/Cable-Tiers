package com.YTrollman.CableTiers.config;

import com.YTrollman.CableTiers.CableTiers;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec client_config;

    static {
        CableConfig.init(client_builder);
        client_config = client_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        CableTiers.LOGGER.info("Loading config: " + path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        CableTiers.LOGGER.info("Built config: " + path);
        file.load();
        CableTiers.LOGGER.info("Loaded config: " + path);
        config.setConfig(file);
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event) {
        CableTiers.LOGGER.debug("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        CableTiers.LOGGER.debug("Config {} just got changed on the file system!", event.getConfig().getFileName());
    }
}