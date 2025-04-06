package com.ultramega.cabletiers.common;

public interface Config {
    SimpleTieredStackEntry getTieredImporters();

    SimpleTieredStackEntry getTieredExporters();

    SimpleTieredEntry getTieredDestructors();

    SimpleTieredStackEntry getTieredConstructors();

    SimpleTieredStackEntry getTieredDiskInterfaces();

    interface SimpleTieredStackEntry extends SimpleTieredEntry {
        boolean hasStackUpgradeIntegrated(CableTiers tier);
    }

    interface SimpleTieredEntry {
        long getEnergyUsage(CableTiers tier);

        int getSpeed(CableTiers tier);
    }
}
