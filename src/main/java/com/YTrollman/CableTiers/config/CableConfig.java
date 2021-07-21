package com.YTrollman.CableTiers.config;

import com.YTrollman.CableTiers.CableTier;
import net.minecraftforge.common.ForgeConfigSpec;

public class CableConfig {

    public static ForgeConfigSpec.IntValue ELITE_EXPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_IMPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_DESTRUCTOR_SPEED;

    public static ForgeConfigSpec.IntValue ULTRA_EXPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_IMPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_DESTRUCTOR_SPEED;

    public static void init(ForgeConfigSpec.Builder client) {
        client.comment("Cable Tiers Options");

        ELITE_EXPORTER_SPEED = client
                .comment("\nElite Exporter Speed")
                .defineInRange("eliteexporterspeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 9);
        ELITE_IMPORTER_SPEED = client
                .comment("\nElite Importer Speed")
                .defineInRange("eliteimporterspeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 9);
        ELITE_CONSTRUCTOR_SPEED = client
                .comment("\nElite Constructor Speed")
                .defineInRange("eliteconstructorspeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);
        ELITE_DESTRUCTOR_SPEED = client
                .comment("\nElite Destructor Speed")
                .defineInRange("elitedestructorspeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);

        ULTRA_EXPORTER_SPEED = client
                .comment("\nUltra Exporter Speed")
                .defineInRange("ultraexporterspeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 9);
        ULTRA_IMPORTER_SPEED = client
                .comment("\nUltra Importer Speed")
                .defineInRange("ultraimporterspeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 9);
        ULTRA_CONSTRUCTOR_SPEED = client
                .comment("\nUltra Constructor Speed")
                .defineInRange("ultraconstructorspeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
        ULTRA_DESTRUCTOR_SPEED = client
                .comment("\nUltra Destructor Speed")
                .defineInRange("ultradestructorspeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
    }
}
