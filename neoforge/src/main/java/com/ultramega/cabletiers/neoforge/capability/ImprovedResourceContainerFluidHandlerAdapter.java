package com.ultramega.cabletiers.neoforge.capability;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.neoforge.support.resource.ResourceContainerFluidHandlerAdapter;

import net.minecraft.world.level.material.Fluids;

/**
 * Check for the slot max stack size instead of the interface export limit
 */
public class ImprovedResourceContainerFluidHandlerAdapter extends ResourceContainerFluidHandlerAdapter {
    private final ResourceContainer container;

    public ImprovedResourceContainerFluidHandlerAdapter(final ResourceContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public int getTankCapacity(final int tank) {
        final ResourceKey resource = container.getResource(tank);
        if (resource == null || resource instanceof FluidResource) {
            return (int) Math.max(this.container.getMaxAmount(resource == null ? new FluidResource(Fluids.EMPTY) : resource),
                ResourceTypes.FLUID.getInterfaceExportLimit());
        }
        return 0;
    }
}
