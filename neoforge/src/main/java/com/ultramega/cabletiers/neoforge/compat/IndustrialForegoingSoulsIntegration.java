package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;

import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.ultramega.refinedtypes.storage.soul.ResourceContainerSoulHandlerAdapter;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class IndustrialForegoingSoulsIntegration {
    private IndustrialForegoingSoulsIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            SoulCapabilities.BLOCK,
            com.refinedmods.refinedstorage.common.content.BlockEntities.INSTANCE.getInterface(),
            (be, side) -> new ResourceContainerSoulHandlerAdapter(be.getExportedResources())
        );
    }
}
