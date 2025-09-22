package com.ultramega.cabletiers.neoforge;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.Config;
import com.ultramega.cabletiers.common.DefaultConfig;

import net.neoforged.neoforge.common.ModConfigSpec;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslationKey;

public class ConfigImpl implements Config {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private final ModConfigSpec spec;

    private final SimpleTieredStackEntry tieredImporters;
    private final SimpleTieredStackEntry tieredExporters;
    private final SimpleTieredEntry tieredDestructors;
    private final SimpleTieredStackEntry tieredConstructors;
    private final SimpleTieredStackEntry tieredDiskInterfaces;
    private final SimpleTieredEntry tieredAutocrafters;

    public ConfigImpl() {
        tieredImporters = new SimpleTieredStackEntryImpl("tieredImporters", CableType.IMPORTER);
        tieredExporters = new SimpleTieredStackEntryImpl("tieredExporters", CableType.EXPORTER);
        tieredDestructors = new SimpleTieredEntryImpl("tieredDestructors", CableType.DESTRUCTOR, true);
        tieredConstructors = new SimpleTieredStackEntryImpl("tieredConstructors", CableType.CONSTRUCTOR);
        tieredDiskInterfaces = new SimpleTieredStackEntryImpl("tieredDiskInterfaces", CableType.DISK_INTERFACE);
        tieredAutocrafters = new SimpleTieredEntryImpl("tieredAutocrafters", CableType.AUTOCRAFTER, true);
        spec = builder.build();
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    @Override
    public SimpleTieredStackEntry getTieredImporters() {
        return tieredImporters;
    }

    @Override
    public SimpleTieredStackEntry getTieredExporters() {
        return tieredExporters;
    }

    @Override
    public SimpleTieredEntry getTieredDestructors() {
        return tieredDestructors;
    }

    @Override
    public SimpleTieredStackEntry getTieredConstructors() {
        return tieredConstructors;
    }

    @Override
    public SimpleTieredStackEntry getTieredDiskInterfaces() {
        return tieredDiskInterfaces;
    }

    @Override
    public SimpleTieredEntry getTieredAutocrafters() {
        return tieredAutocrafters;
    }

    private static String translationKey(final String value) {
        return createCableTiersTranslationKey("text.autoconfig", "option." + value);
    }

    private class SimpleTieredStackEntryImpl extends SimpleTieredEntryImpl implements SimpleTieredStackEntry {
        private final ModConfigSpec.BooleanValue eliteStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue ultraStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue megaStackUpgradeIntegrated;

        SimpleTieredStackEntryImpl(final String name, final CableType type) {
            super(name, type, false);

            eliteStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".eliteStackUpgradeIntegrated"))
                .define("eliteStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegrated(CableTiers.ELITE, type));
            ultraStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".ultraStackUpgradeIntegrated"))
                .define("ultraStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegrated(CableTiers.ULTRA, type));
            megaStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".megaStackUpgradeIntegrated"))
                .define("megaStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegrated(CableTiers.MEGA, type));

            builder.pop();
        }

        @Override
        public boolean hasStackUpgradeIntegrated(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> eliteStackUpgradeIntegrated.get();
                case ULTRA -> ultraStackUpgradeIntegrated.get();
                case MEGA -> megaStackUpgradeIntegrated.get();
                case CREATIVE -> true;
            };
        }
    }

    private class SimpleTieredEntryImpl implements SimpleTieredEntry {
        private final ModConfigSpec.LongValue eliteEnergyUsage;
        private final ModConfigSpec.LongValue ultraEnergyUsage;
        private final ModConfigSpec.LongValue megaEnergyUsage;

        private final ModConfigSpec.IntValue eliteSpeed;
        private final ModConfigSpec.IntValue ultraSpeed;
        private final ModConfigSpec.IntValue megaSpeed;
        private final ModConfigSpec.IntValue creativeSpeed;

        SimpleTieredEntryImpl(final String name, final CableType type, final boolean pop) {
            builder.translation(translationKey(name)).push(name);
            eliteEnergyUsage = builder
                .translation(translationKey(name + ".eliteEnergyUsage"))
                .defineInRange("eliteEnergyUsage", DefaultConfig.getUsageFor(CableTiers.ELITE, type), 0, Long.MAX_VALUE);
            ultraEnergyUsage = builder
                .translation(translationKey(name + ".ultraEnergyUsage"))
                .defineInRange("ultraEnergyUsage", DefaultConfig.getUsageFor(CableTiers.ULTRA, type), 0, Long.MAX_VALUE);
            megaEnergyUsage = builder
                .translation(translationKey(name + ".megaEnergyUsage"))
                .defineInRange("megaEnergyUsage", DefaultConfig.getUsageFor(CableTiers.MEGA, type), 0, Long.MAX_VALUE);

            eliteSpeed = builder
                .translation(translationKey(name + ".eliteSpeed"))
                .defineInRange("eliteSpeed", DefaultConfig.getSpeedFor(CableTiers.ELITE, type), 1, Integer.MAX_VALUE);
            ultraSpeed = builder
                .translation(translationKey(name + ".ultraSpeed"))
                .defineInRange("ultraSpeed", DefaultConfig.getSpeedFor(CableTiers.ULTRA, type), 1, Integer.MAX_VALUE);
            megaSpeed = builder
                .translation(translationKey(name + ".megaSpeed"))
                .defineInRange("megaSpeed", DefaultConfig.getSpeedFor(CableTiers.MEGA, type), 1, Integer.MAX_VALUE);
            creativeSpeed = builder
                .translation(translationKey(name + ".creativeSpeed"))
                .defineInRange("creativeSpeed", DefaultConfig.getSpeedFor(CableTiers.CREATIVE, type), 1, Integer.MAX_VALUE);

            if (pop) {
                builder.pop();
            }
        }

        @Override
        public long getEnergyUsage(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> eliteEnergyUsage.get();
                case ULTRA -> ultraEnergyUsage.get();
                case MEGA -> megaEnergyUsage.get();
                case CREATIVE -> 0;
            };
        }

        @Override
        public int getSpeed(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> eliteSpeed.get();
                case ULTRA -> ultraSpeed.get();
                case MEGA -> megaSpeed.get();
                case CREATIVE -> creativeSpeed.get();
            };
        }
    }
}
