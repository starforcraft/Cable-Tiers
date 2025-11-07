package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.BlockEntities;

import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.ultramega.refinedtypes.storage.soul.ResourceContainerSoulHandlerAdapter;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class IndustrialForegoingSoulsIntegration {
    private IndustrialForegoingSoulsIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            SoulCapabilities.BLOCK,
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            (be, side) -> new ResourceContainerSoulHandlerAdapter(be.getExportedResources())
        );
    }
}
