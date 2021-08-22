package com.YTrollman.CableTiers.config;

import com.YTrollman.CableTiers.CableTier;
import net.minecraftforge.common.ForgeConfigSpec;

public class CableConfig {

    public static ForgeConfigSpec.IntValue ELITE_EXPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_IMPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_DESTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_DISK_MANIPULATOR_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_REQUESTER_MAX_CRAFT_AMOUNT;
    public static ForgeConfigSpec.IntValue ELITE_ENERGY_COST;

    public static ForgeConfigSpec.IntValue ULTRA_EXPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_IMPORTER_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_DESTRUCTOR_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_DISK_MANIPULATOR_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_REQUESTER_MAX_CRAFT_AMOUNT;
    public static ForgeConfigSpec.IntValue ULTRA_ENERGY_COST;

    public static ForgeConfigSpec.IntValue CREATIVE_ENERGY_COST;

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
        ELITE_DISK_MANIPULATOR_SPEED = client
                .comment("\nElite Disk Manipulator Speed")
                .defineInRange("elitediskmanipulatorspeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);
        ELITE_REQUESTER_MAX_CRAFT_AMOUNT = client
                .comment("\nElite Requester Max Craft Amount")
                .defineInRange("eliterequestermaxcraftamount", 2000, 1, Integer.MAX_VALUE);
        ELITE_ENERGY_COST = client
                .comment("\nElite Energy Cost Multiplier")
                .defineInRange("eliteenergycost", 2, 0, Integer.MAX_VALUE);

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
        ULTRA_DISK_MANIPULATOR_SPEED = client
                .comment("\nUltra Disk Manipulator Speed")
                .defineInRange("ultradiskmanipulatorspeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
        ULTRA_REQUESTER_MAX_CRAFT_AMOUNT = client
                .comment("\nUltra Requester Max Craft Amount")
                .defineInRange("ultrarequestermaxcraftamount", 6000, 1, Integer.MAX_VALUE);
        ULTRA_ENERGY_COST = client
                .comment("\nUltra Energy Cost Multiplier")
                .defineInRange("ultraenergycost", 4, 0, Integer.MAX_VALUE);

        CREATIVE_ENERGY_COST = client
                .comment("\nCreative Energy Cost Multiplier")
                .defineInRange("creativeenergycost", 0, 0, Integer.MAX_VALUE);
    }
}
