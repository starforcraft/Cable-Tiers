package com.ultramega.cabletiers.fabric;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.DefaultConfig;
import com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = CableTiersIdentifierUtil.MOD_ID)
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "CanBeFinal"})
public class ConfigImpl implements ConfigData, com.ultramega.cabletiers.common.Config {
    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredStackEntryImpl tieredImporters = new SimpleTieredStackEntryImpl(CableType.IMPORTER);

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredStackEntryImpl tieredExporters = new SimpleTieredStackEntryImpl(CableType.EXPORTER);

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredEntryImpl tieredDestructors = new SimpleTieredEntryImpl(CableType.DESTRUCTOR);

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredStackEntryImpl tieredConstructors = new SimpleTieredStackEntryImpl(CableType.CONSTRUCTOR);

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredStackEntryImpl tieredDiskInterface = new SimpleTieredStackEntryImpl(CableType.DISK_INTERFACE); // Removed "s" to force configs to be regenerated

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredEntryImpl tieredAutocrafters = new SimpleTieredEntryImpl(CableType.AUTOCRAFTER);

    @ConfigEntry.Gui.CollapsibleObject
    private SimpleTieredInterfaceEntryImpl tieredInterfaces = new SimpleTieredInterfaceEntryImpl(CableType.INTERFACE);

    public static ConfigImpl get() {
        return AutoConfig.getConfigHolder(ConfigImpl.class).getConfig();
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
    public SimpleTieredStackEntry getTieredDiskInterfaces() {
        return this.tieredDiskInterface;
    }

    @Override
    public SimpleTieredEntry getTieredAutocrafters() {
        return this.tieredAutocrafters;
    }

    @Override
    public SimpleTieredInterfaceEntry getTieredInterfaces() {
        return this.tieredInterfaces;
    }

    private static class SimpleTieredStackEntryImpl implements SimpleTieredStackEntry {
        private long eliteEnergyUsage;
        private long ultraEnergyUsage;
        private long megaEnergyUsage;

        private int eliteSpeed;
        private int ultraSpeed;
        private int megaSpeed;
        private int creativeSpeed;

        private boolean eliteStackUpgradeIntegrated;
        private boolean ultraStackUpgradeIntegrated;
        private boolean megaStackUpgradeIntegrated;

        SimpleTieredStackEntryImpl(final CableType type) {
            this.eliteEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ELITE, type);
            this.ultraEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ULTRA, type);
            this.megaEnergyUsage = DefaultConfig.getUsageFor(CableTiers.MEGA, type);

            this.eliteSpeed = DefaultConfig.getSpeedFor(CableTiers.ELITE, type);
            this.ultraSpeed = DefaultConfig.getSpeedFor(CableTiers.ULTRA, type);
            this.megaSpeed = DefaultConfig.getSpeedFor(CableTiers.MEGA, type);
            this.creativeSpeed = DefaultConfig.getSpeedFor(CableTiers.MEGA, type);

            this.eliteStackUpgradeIntegrated = DefaultConfig.isStackUpgradeIntegrated(CableTiers.ELITE, type);
            this.ultraStackUpgradeIntegrated = DefaultConfig.isStackUpgradeIntegrated(CableTiers.ULTRA, type);
            this.megaStackUpgradeIntegrated = DefaultConfig.isStackUpgradeIntegrated(CableTiers.MEGA, type);
        }

        @Override
        public long getEnergyUsage(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteEnergyUsage;
                case ULTRA -> this.ultraEnergyUsage;
                case MEGA -> this.megaEnergyUsage;
                case CREATIVE -> 0;
            };
        }

        @Override
        public int getSpeed(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteSpeed;
                case ULTRA -> this.ultraSpeed;
                case MEGA -> this.megaSpeed;
                case CREATIVE -> this.creativeSpeed;
            };
        }

        @Override
        public boolean hasStackUpgradeIntegrated(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteStackUpgradeIntegrated;
                case ULTRA -> this.ultraStackUpgradeIntegrated;
                case MEGA -> this.megaStackUpgradeIntegrated;
                case CREATIVE -> true;
            };
        }
    }

    private static class SimpleTieredInterfaceEntryImpl implements SimpleTieredInterfaceEntry {
        private long eliteEnergyUsage;
        private long ultraEnergyUsage;
        private long megaEnergyUsage;

        private long eliteTransferQuotaMultiplier;
        private long ultraTransferQuotaMultiplier;
        private long megaTransferQuotaMultiplier;
        private long creativeTransferQuotaMultiplier;

        SimpleTieredInterfaceEntryImpl(final CableType type) {
            this.eliteEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ELITE, type);
            this.ultraEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ULTRA, type);
            this.megaEnergyUsage = DefaultConfig.getUsageFor(CableTiers.MEGA, type);

            this.eliteTransferQuotaMultiplier = DefaultConfig.getTransferQuotaMultiplier(CableTiers.ELITE, type);
            this.ultraTransferQuotaMultiplier = DefaultConfig.getTransferQuotaMultiplier(CableTiers.ULTRA, type);
            this.megaTransferQuotaMultiplier = DefaultConfig.getTransferQuotaMultiplier(CableTiers.MEGA, type);
            this.creativeTransferQuotaMultiplier = DefaultConfig.getTransferQuotaMultiplier(CableTiers.CREATIVE, type);
        }

        @Override
        public long getEnergyUsage(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteEnergyUsage;
                case ULTRA -> this.ultraEnergyUsage;
                case MEGA -> this.megaEnergyUsage;
                case CREATIVE -> 0;
            };
        }

        @Override
        public long getTransferQuotaMultiplier(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteTransferQuotaMultiplier;
                case ULTRA -> this.ultraTransferQuotaMultiplier;
                case MEGA -> this.megaTransferQuotaMultiplier;
                case CREATIVE -> this.creativeTransferQuotaMultiplier;
            };
        }
    }

    private static class SimpleTieredEntryImpl implements SimpleTieredEntry {
        private long eliteEnergyUsage;
        private long ultraEnergyUsage;
        private long megaEnergyUsage;

        private int eliteSpeed;
        private int ultraSpeed;
        private int megaSpeed;
        private int creativeSpeed;

        SimpleTieredEntryImpl(final CableType type) {
            this.eliteEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ELITE, type);
            this.ultraEnergyUsage = DefaultConfig.getUsageFor(CableTiers.ULTRA, type);
            this.megaEnergyUsage = DefaultConfig.getUsageFor(CableTiers.MEGA, type);

            this.eliteSpeed = DefaultConfig.getSpeedFor(CableTiers.ELITE, type);
            this.ultraSpeed = DefaultConfig.getSpeedFor(CableTiers.ULTRA, type);
            this.megaSpeed = DefaultConfig.getSpeedFor(CableTiers.MEGA, type);
            this.creativeSpeed = DefaultConfig.getSpeedFor(CableTiers.MEGA, type);
        }

        @Override
        public long getEnergyUsage(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteEnergyUsage;
                case ULTRA -> this.ultraEnergyUsage;
                case MEGA -> this.megaEnergyUsage;
                case CREATIVE -> 0;
            };
        }

        @Override
        public int getSpeed(final CableTiers tier) {
            return switch (tier) {
                case ELITE -> this.eliteSpeed;
                case ULTRA -> this.ultraSpeed;
                case MEGA -> this.megaSpeed;
                case CREATIVE -> this.creativeSpeed;
            };
        }
    }
}
