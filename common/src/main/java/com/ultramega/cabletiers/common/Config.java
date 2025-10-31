package com.ultramega.cabletiers.common;

public interface Config {
    SimpleTieredStackEntry getTieredImporters();

    SimpleTieredStackEntry getTieredExporters();

    SimpleTieredEntry getTieredDestructors();

    SimpleTieredStackEntry getTieredConstructors();

    SimpleTieredStackEntry getTieredDiskInterfaces(); //TODO: remove speed config out of disk interface because it does literally nothing anymore

    SimpleTieredEntry getTieredAutocrafters();

    SimpleTieredInterfaceEntry getTieredInterfaces();

    interface SimpleTieredStackEntry extends SimpleTieredEntry {
        boolean hasStackUpgradeIntegrated(CableTiers tier);
    }

    interface SimpleTieredInterfaceEntry extends SimpleTieredEnergyEntry {
        long getTransferQuotaMultiplier(CableTiers tier);
    }

    interface SimpleTieredEntry extends SimpleTieredEnergyEntry {
        int getSpeed(CableTiers tier);
    }

    interface SimpleTieredEnergyEntry {
        long getEnergyUsage(CableTiers tier);
    }
}
