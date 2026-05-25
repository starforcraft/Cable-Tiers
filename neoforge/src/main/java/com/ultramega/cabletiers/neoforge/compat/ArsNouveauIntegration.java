package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ArsNouveauIntegration {
    private ArsNouveauIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        /*event.registerBlockEntity(
            CapabilityRegistry.SOURCE_CAPABILITY,
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            (be, side) -> new ResourceContainerSourceHandlerAdapter(be.getExportedResources())
        );*/
    }
}
