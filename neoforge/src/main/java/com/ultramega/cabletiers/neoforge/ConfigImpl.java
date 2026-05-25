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
    private final SimpleTieredStackEnergyEntry tieredDiskInterfaces;
    private final SimpleTieredAutocrafterEntry tieredAutocrafters;
    private final SimpleTieredInterfaceEntry tieredInterfaces;

    public ConfigImpl() {
        this.tieredImporters = new SimpleTieredStackEntryImpl("tieredImporters", CableType.IMPORTER);
        this.tieredExporters = new SimpleTieredStackEntryImpl("tieredExporters", CableType.EXPORTER);
        this.tieredDestructors = new SimpleTieredEntryImpl("tieredDestructors", CableType.DESTRUCTOR, true);
        this.tieredConstructors = new SimpleTieredStackEntryImpl("tieredConstructors", CableType.CONSTRUCTOR);
        this.tieredDiskInterfaces = new SimpleTieredStackEnergyEntryImpl("tieredDiskInterfaces", CableType.DISK_INTERFACE);
        this.tieredAutocrafters = new SimpleTieredAutocrafterEntryImpl("tieredAutocrafters", CableType.AUTOCRAFTER);
        this.tieredInterfaces = new SimpleTieredInterfaceEntryImpl("tieredInterfaces", CableType.INTERFACE);
        this.spec = this.builder.build();
    }

    public ModConfigSpec getSpec() {
        return this.spec;
    }

    @Override
    public SimpleTieredStackEntry getTieredImporters() {
        return this.tieredImporters;
    }

    @Override
    public SimpleTieredStackEntry getTieredExporters() {
        return this.tieredExporters;
    }

    @Override
    public SimpleTieredEntry getTieredDestructors() {
        return this.tieredDestructors;
    }

    @Override
    public SimpleTieredStackEntry getTieredConstructors() {
        return this.tieredConstructors;
    }

    @Override
    public SimpleTieredStackEnergyEntry getTieredDiskInterfaces() {
        return this.tieredDiskInterfaces;
    }

    @Override
    public SimpleTieredAutocrafterEntry getTieredAutocrafters() {
        return this.tieredAutocrafters;
    }

    @Override
    public SimpleTieredInterfaceEntry getTieredInterfaces() {
        return this.tieredInterfaces;
    }

    private static String translationKey(final String value) {
        return createCableTiersTranslationKey("text.autoconfig", "option." + value);
    }

    private final class SimpleTieredStackEntryImpl extends SimpleTieredEntryImpl implements SimpleTieredStackEntry {
        private final ModConfigSpec.BooleanValue eliteStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue ultraStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue megaStackUpgradeIntegrated;

        SimpleTieredStackEntryImpl(final String name, final CableType type) {
            super(name, type, false);

            this.eliteStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".eliteStackUpgradeIntegrated"))
                .define("eliteStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.ELITE, type));
            this.ultraStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".ultraStackUpgradeIntegrated"))
                .define("ultraStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.ULTRA, type));
            this.megaStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".megaStackUpgradeIntegrated"))
                .define("megaStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.MEGA, type));

            builder.pop();
        }

        @Override
        public boolean hasStackUpgradeIntegrated(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteStackUpgradeIntegrated.get();
                case ULTRA -> this.ultraStackUpgradeIntegrated.get();
                case MEGA -> this.megaStackUpgradeIntegrated.get();
                case CREATIVE -> true;
            };
        }
    }

    private final class SimpleTieredStackEnergyEntryImpl extends SimpleTieredEnergyEntryImpl implements SimpleTieredStackEnergyEntry {
        private final ModConfigSpec.BooleanValue eliteStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue ultraStackUpgradeIntegrated;
        private final ModConfigSpec.BooleanValue megaStackUpgradeIntegrated;

        SimpleTieredStackEnergyEntryImpl(final String name, final CableType type) {
            super(name, type, false);

            this.eliteStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".eliteStackUpgradeIntegrated"))
                .define("eliteStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.ELITE, type));
            this.ultraStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".ultraStackUpgradeIntegrated"))
                .define("ultraStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.ULTRA, type));
            this.megaStackUpgradeIntegrated = builder
                .translation(translationKey(name + ".megaStackUpgradeIntegrated"))
                .define("megaStackUpgradeIntegrated", DefaultConfig.isStackUpgradeIntegratedFor(CableTiers.MEGA, type));

            builder.pop();
        }

        @Override
        public boolean hasStackUpgradeIntegrated(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteStackUpgradeIntegrated.get();
                case ULTRA -> this.ultraStackUpgradeIntegrated.get();
                case MEGA -> this.megaStackUpgradeIntegrated.get();
                case CREATIVE -> true;
            };
        }
    }

    private final class SimpleTieredInterfaceEntryImpl extends SimpleTieredEnergyEntryImpl implements SimpleTieredInterfaceEntry {
        private final ModConfigSpec.LongValue eliteTransferQuotaMultiplier;
        private final ModConfigSpec.LongValue ultraTransferQuotaMultiplier;
        private final ModConfigSpec.LongValue megaTransferQuotaMultiplier;
        private final ModConfigSpec.LongValue creativeTransferQuotaMultiplier;

        SimpleTieredInterfaceEntryImpl(final String name, final CableType type) {
            super(name, type, false);

            this.eliteTransferQuotaMultiplier = builder
                .translation(translationKey(name + ".eliteTransferQuotaMultiplier"))
                .defineInRange("eliteTransferQuotaMultiplier", DefaultConfig.getTransferQuotaMultiplierFor(CableTiers.ELITE, type), 1, Long.MAX_VALUE);
            this.ultraTransferQuotaMultiplier = builder
                .translation(translationKey(name + ".ultraTransferQuotaMultiplier"))
                .defineInRange("ultraTransferQuotaMultiplier", DefaultConfig.getTransferQuotaMultiplierFor(CableTiers.ULTRA, type), 1, Long.MAX_VALUE);
            this.megaTransferQuotaMultiplier = builder
                .translation(translationKey(name + ".megaTransferQuotaMultiplier"))
                .defineInRange("megaTransferQuotaMultiplier", DefaultConfig.getTransferQuotaMultiplierFor(CableTiers.MEGA, type), 1, Long.MAX_VALUE);
            this.creativeTransferQuotaMultiplier = builder
                .translation(translationKey(name + ".creativeTransferQuotaMultiplier"))
                .defineInRange("creativeTransferQuotaMultiplier", DefaultConfig.getTransferQuotaMultiplierFor(CableTiers.CREATIVE, type), 1, Long.MAX_VALUE);

            builder.pop();
        }

        @Override
        public long getTransferQuotaMultiplier(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteTransferQuotaMultiplier.get();
                case ULTRA -> this.ultraTransferQuotaMultiplier.get();
                case MEGA -> this.megaTransferQuotaMultiplier.get();
                case CREATIVE -> this.creativeTransferQuotaMultiplier.get();
            };
        }
    }

    private final class SimpleTieredAutocrafterEntryImpl extends SimpleTieredEntryImpl implements SimpleTieredAutocrafterEntry {
        private final ModConfigSpec.IntValue ultraPatternSlotCount;
        private final ModConfigSpec.IntValue megaPatternSlotCount;
        private final ModConfigSpec.IntValue creativePatternSlotCount;

        SimpleTieredAutocrafterEntryImpl(final String name, final CableType type) {
            super(name, type, false);

            this.ultraPatternSlotCount = builder
                .translation(translationKey(name + ".ultraPatternSlotCount"))
                .defineInRange("ultraPatternSlotCount", DefaultConfig.getPatternSlotCountFor(CableTiers.ULTRA, type), 9 * 6, Integer.MAX_VALUE);
            this.megaPatternSlotCount = builder
                .translation(translationKey(name + ".megaPatternSlotCount"))
                .defineInRange("megaPatternSlotCount", DefaultConfig.getPatternSlotCountFor(CableTiers.MEGA, type), 9 * 6, Integer.MAX_VALUE);
            this.creativePatternSlotCount = builder
                .translation(translationKey(name + ".creativePatternSlotCount"))
                .defineInRange("creativePatternSlotCount", DefaultConfig.getPatternSlotCountFor(CableTiers.CREATIVE, type), 9 * 6, Integer.MAX_VALUE);

            builder.pop();
        }

        @Override
        public int getPatternSlotCount(final CableTiers tier) {
            return switch (tier) {
                case ULTRA -> this.ultraPatternSlotCount.get();
                case MEGA -> this.megaPatternSlotCount.get();
                case CREATIVE -> this.creativePatternSlotCount.get();
                default -> throw new UnsupportedOperationException(tier.getLowercaseName() + " has no pattern slot count config");
            };
        }
    }

    private class SimpleTieredEntryImpl extends SimpleTieredEnergyEntryImpl implements SimpleTieredEntry {
        private final ModConfigSpec.IntValue eliteSpeed;
        private final ModConfigSpec.IntValue ultraSpeed;
        private final ModConfigSpec.IntValue megaSpeed;
        private final ModConfigSpec.IntValue creativeSpeed;

        SimpleTieredEntryImpl(final String name, final CableType type, final boolean pop) {
            super(name, type, false);

            this.eliteSpeed = builder
                .translation(translationKey(name + ".eliteSpeed"))
                .defineInRange("eliteSpeed", DefaultConfig.getSpeedFor(CableTiers.ELITE, type), 1, Integer.MAX_VALUE);
            this.ultraSpeed = builder
                .translation(translationKey(name + ".ultraSpeed"))
                .defineInRange("ultraSpeed", DefaultConfig.getSpeedFor(CableTiers.ULTRA, type), 1, Integer.MAX_VALUE);
            this.megaSpeed = builder
                .translation(translationKey(name + ".megaSpeed"))
                .defineInRange("megaSpeed", DefaultConfig.getSpeedFor(CableTiers.MEGA, type), 1, Integer.MAX_VALUE);
            this.creativeSpeed = builder
                .translation(translationKey(name + ".creativeSpeed"))
                .defineInRange("creativeSpeed", DefaultConfig.getSpeedFor(CableTiers.CREATIVE, type), 1, Integer.MAX_VALUE);

            if (pop) {
                builder.pop();
            }
        }

        @Override
        public int getSpeed(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteSpeed.get();
                case ULTRA -> this.ultraSpeed.get();
                case MEGA -> this.megaSpeed.get();
                case CREATIVE -> this.creativeSpeed.get();
            };
        }
    }

    private class SimpleTieredEnergyEntryImpl implements SimpleTieredEnergyEntry {
        private final ModConfigSpec.LongValue eliteEnergyUsage;
        private final ModConfigSpec.LongValue ultraEnergyUsage;
        private final ModConfigSpec.LongValue megaEnergyUsage;

        SimpleTieredEnergyEntryImpl(final String name, final CableType type, final boolean pop) {
            builder.translation(translationKey(name)).push(name);

            this.eliteEnergyUsage = builder
                .translation(translationKey(name + ".eliteEnergyUsage"))
                .defineInRange("eliteEnergyUsage", DefaultConfig.getUsageFor(CableTiers.ELITE, type), 0, Long.MAX_VALUE);
            this.ultraEnergyUsage = builder
                .translation(translationKey(name + ".ultraEnergyUsage"))
                .defineInRange("ultraEnergyUsage", DefaultConfig.getUsageFor(CableTiers.ULTRA, type), 0, Long.MAX_VALUE);
            this.megaEnergyUsage = builder
                .translation(translationKey(name + ".megaEnergyUsage"))
                .defineInRange("megaEnergyUsage", DefaultConfig.getUsageFor(CableTiers.MEGA, type), 0, Long.MAX_VALUE);

            if (pop) {
                builder.pop();
            }
        }

        @Override
        public long getEnergyUsage(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteEnergyUsage.get();
                case ULTRA -> this.ultraEnergyUsage.get();
                case MEGA -> this.megaEnergyUsage.get();
                case CREATIVE -> 0;
            };
        }
    }
}
