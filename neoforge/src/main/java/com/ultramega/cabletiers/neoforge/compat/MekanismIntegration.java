package com.ultramega.cabletiers.neoforge.compat;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.neoforge.capability.ImprovedResourceContainerChemicalHandlerAdapter;

import com.refinedmods.refinedstorage.mekanism.ChemicalUtil;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class MekanismIntegration {
    private MekanismIntegration() {
    }

    public static void registerCapabilities(final CableTiers tier, final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            ChemicalUtil.BLOCK_CAPABILITY,
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            (be, side) -> new ImprovedResourceContainerChemicalHandlerAdapter(be.getExportedResources())
        );
    }
}
