package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class RefinedTypesIntegration {
    private RefinedTypesIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        /*event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            (be, side) -> new ResourceContainerEnergyHandlerAdapter(be.getExportedResources())
        );*/
    }
}
