package com.ultramega.cabletiers.config;

import com.ultramega.cabletiers.CableTier;
import net.minecraftforge.common.ForgeConfigSpec;

public class CableConfig {
    public static ForgeConfigSpec.DoubleValue ELITE_EXPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue ELITE_IMPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue ELITE_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue ELITE_DESTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue ELITE_DISK_MANIPULATOR_SPEED;
    public static ForgeConfigSpec.IntValue ELITE_REQUESTER_MAX_CRAFT_AMOUNT;
    public static ForgeConfigSpec.IntValue ELITE_ENERGY_COST;

    public static ForgeConfigSpec.DoubleValue ULTRA_EXPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue ULTRA_IMPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue ULTRA_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue ULTRA_DESTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue ULTRA_DISK_MANIPULATOR_SPEED;
    public static ForgeConfigSpec.IntValue ULTRA_REQUESTER_MAX_CRAFT_AMOUNT;
    public static ForgeConfigSpec.IntValue ULTRA_ENERGY_COST;

    public static ForgeConfigSpec.DoubleValue MEGA_EXPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue MEGA_IMPORTER_SPEED;
    public static ForgeConfigSpec.DoubleValue MEGA_CONSTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue MEGA_DESTRUCTOR_SPEED;
    public static ForgeConfigSpec.DoubleValue MEGA_DISK_MANIPULATOR_SPEED;
    public static ForgeConfigSpec.IntValue MEGA_REQUESTER_MAX_CRAFT_AMOUNT;
    public static ForgeConfigSpec.IntValue MEGA_ENERGY_COST;

    public static void init(ForgeConfigSpec.Builder common) {
        common.push("Elite Tier Options");
            ELITE_EXPORTER_SPEED = common.comment("\nElite Exporter Speed").defineInRange("eliteExporterSpeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 9);
            ELITE_IMPORTER_SPEED = common.comment("\nElite Importer Speed").defineInRange("eliteImporterSpeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 9);
            ELITE_CONSTRUCTOR_SPEED = common.comment("\nElite Constructor Speed").defineInRange("eliteConstructorSpeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);
            ELITE_DESTRUCTOR_SPEED = common.comment("\nElite Destructor Speed").defineInRange("eliteDestructorSpeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);
            ELITE_DISK_MANIPULATOR_SPEED = common.comment("\nElite Disk Manipulator Speed").defineInRange("eliteDiskManipulatorSpeed", CableTier.ELITE.getDefaultSpeedMultiplier(), 1, 20);
            ELITE_REQUESTER_MAX_CRAFT_AMOUNT = common.comment("\nElite Requester Max Craft Amount").defineInRange("eliteRequesterMaxCraftAmount", 2000, 1, Integer.MAX_VALUE);
            ELITE_ENERGY_COST = common.comment("\nElite Energy Cost Multiplier").defineInRange("eliteEnergyCost", 2, 0, Integer.MAX_VALUE);
        common.pop();

        common.push("Ultra Tier Options");
            ULTRA_EXPORTER_SPEED = common.comment("\nUltra Exporter Speed").defineInRange("ultraExporterSpeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 9);
            ULTRA_IMPORTER_SPEED = common.comment("\nUltra Importer Speed").defineInRange("ultraImporterSpeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 9);
            ULTRA_CONSTRUCTOR_SPEED = common.comment("\nUltra Constructor Speed").defineInRange("ultraConstructorSpeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
            ULTRA_DESTRUCTOR_SPEED = common.comment("\nUltra Destructor Speed").defineInRange("ultraDestructorSpeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
            ULTRA_DISK_MANIPULATOR_SPEED = common.comment("\nUltra Disk Manipulator Speed").defineInRange("ultraDiskManipulatorSpeed", CableTier.ULTRA.getDefaultSpeedMultiplier(), 1, 20);
            ULTRA_REQUESTER_MAX_CRAFT_AMOUNT = common.comment("\nUltra Requester Max Craft Amount").defineInRange("ultraRequesterMaxCraftAmount", 4000, 1, Integer.MAX_VALUE);
            ULTRA_ENERGY_COST = common.comment("\nUltra Energy Cost Multiplier").defineInRange("ultraEnergyCost", 3, 0, Integer.MAX_VALUE);
        common.pop();

        common.push("Mega Tier Options");
            MEGA_EXPORTER_SPEED = common.comment("\nMega Exporter Speed").defineInRange("megaExporterSpeed", CableTier.MEGA.getDefaultSpeedMultiplier(), 1, 9);
            MEGA_IMPORTER_SPEED = common.comment("\nMega Importer Speed").defineInRange("megaImporterSpeed", CableTier.MEGA.getDefaultSpeedMultiplier(), 1, 9);
            MEGA_CONSTRUCTOR_SPEED = common.comment("\nMega Constructor Speed").defineInRange("megaConstructorSpeed", CableTier.MEGA.getDefaultSpeedMultiplier(), 1, 20);
            MEGA_DESTRUCTOR_SPEED = common.comment("\nMega Destructor Speed").defineInRange("megaDestructorSpeed", CableTier.MEGA.getDefaultSpeedMultiplier(), 1, 20);
            MEGA_DISK_MANIPULATOR_SPEED = common.comment("\nMega Disk Manipulator Speed").defineInRange("megaDiskManipulatorSpeed", CableTier.MEGA.getDefaultSpeedMultiplier(), 1, 20);
            MEGA_REQUESTER_MAX_CRAFT_AMOUNT = common.comment("\nMega Requester Max Craft Amount").defineInRange("megaRequesterMaxCraftAmount", 6000, 1, Integer.MAX_VALUE);
            MEGA_ENERGY_COST = common.comment("\nMega Energy Cost Multiplier").defineInRange("megaEnergyCost", 4, 0, Integer.MAX_VALUE);
        common.pop();

        common.build();
    }
}
