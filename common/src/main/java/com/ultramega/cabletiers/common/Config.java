package com.ultramega.cabletiers.common;

public interface Config {
    SimpleTieredStackEntry getTieredImporters();

    SimpleTieredStackEntry getTieredExporters();

    SimpleTieredEntry getTieredDestructors();

    SimpleTieredStackEntry getTieredConstructors();

    SimpleTieredStackEnergyEntry getTieredDiskInterfaces();

    SimpleTieredAutocrafterEntry getTieredAutocrafters();

    SimpleTieredInterfaceEntry getTieredInterfaces();

    interface SimpleTieredStackEnergyEntry extends SimpleTieredEnergyEntry {
        boolean hasStackUpgradeIntegrated(CableTiers tier);
    }

    interface SimpleTieredStackEntry extends SimpleTieredEntry, SimpleTieredStackEnergyEntry {
    }

    interface SimpleTieredInterfaceEntry extends SimpleTieredEnergyEntry {
        long getTransferQuotaMultiplier(CableTiers tier);
    }

    interface SimpleTieredAutocrafterEntry extends SimpleTieredEntry {
        int getPatternSlotCount(CableTiers tier);
    }

    interface SimpleTieredEntry extends SimpleTieredEnergyEntry {
        int getSpeed(CableTiers tier);
    }

    interface SimpleTieredEnergyEntry {
        long getEnergyUsage(CableTiers tier);
    }
}
