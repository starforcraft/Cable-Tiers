package com.YTrollman.CableTiers.config;

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
                    .defineInRange("eliteexporterspeed", 2, 1, 10);
            ELITE_IMPORTER_SPEED = client
            		.comment("\nElite Importer Speed")
                    .defineInRange("eliteimporterspeed", 2, 1, 10);
            ELITE_CONSTRUCTOR_SPEED = client
            		.comment("\nElite Constructor Speed")
                    .defineInRange("eliteconstructorspeed", 2, 1, 10);
            ELITE_DESTRUCTOR_SPEED = client
            		.comment("\nElite Destructor Speed")
                    .defineInRange("elitedestructorspeed", 2, 1, 10);
            
            ULTRA_EXPORTER_SPEED = client
            		.comment("\nUltra Exporter Speed")
                    .defineInRange("ultraexporterspeed", 6, 1, 10);
            ULTRA_IMPORTER_SPEED = client
            		.comment("\nUltra Importer Speed")
                    .defineInRange("ultraimporterspeed", 6, 1, 10);
            ULTRA_CONSTRUCTOR_SPEED = client
            		.comment("\nUltra Constructor Speed")
                    .defineInRange("ultraconstructorspeed", 6, 1, 10);
            ULTRA_DESTRUCTOR_SPEED = client
            		.comment("\nUltra Destructor Speed")
                    .defineInRange("ultradestructorspeed", 6, 1, 10);
    }
}
