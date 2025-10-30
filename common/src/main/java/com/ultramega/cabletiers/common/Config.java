package com.ultramega.cabletiers.common;

public interface Config {
    SimpleTieredStackEntry getTieredImporters();

    SimpleTieredStackEntry getTieredExporters();

    SimpleTieredEntry getTieredDestructors();

    SimpleTieredStackEntry getTieredConstructors();

    SimpleTieredStackEntry getTieredDiskInterfaces();

    SimpleTieredEntry getTieredAutocrafters();

    SimpleTieredInterfaceEntry getTieredInterfaces();

    interface SimpleTieredStackEntry extends SimpleTieredEntry {
        boolean hasStackUpgradeIntegrated(CableTiers tier);
    }

    interface SimpleTieredInterfaceEntry extends SimpleTieredEntry {
        long getTransferQuotaMultiplier(CableTiers tier);
    }

    interface SimpleTieredEntry {
        long getEnergyUsage(CableTiers tier);

        int getSpeed(CableTiers tier);
    }
}
