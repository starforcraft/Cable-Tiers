package com.ultramega.cabletiers.config;

import com.ultramega.cabletiers.CableTiers;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec common_config;

    private static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();

    static {
        CableConfig.init(common_builder);
        common_config = common_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        CableTiers.LOGGER.info("Loading config: " + path);
        CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        CableTiers.LOGGER.info("Built config: " + path);
        file.load();
        CableTiers.LOGGER.info("Loaded config: " + path);
        config.setConfig(file);
    }
}
