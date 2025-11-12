package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.BlockEntities;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.ultramega.refinedtypes.storage.source.ResourceContainerSourceHandlerAdapter;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ArsNouveauIntegration {
    private ArsNouveauIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            CapabilityRegistry.SOURCE_CAPABILITY,
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            (be, side) -> new ResourceContainerSourceHandlerAdapter(be.getExportedResources())
        );
    }
}
