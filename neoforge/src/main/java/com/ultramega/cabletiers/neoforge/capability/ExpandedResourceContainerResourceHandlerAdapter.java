package com.ultramega.cabletiers.neoforge.capability;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.neoforge.support.resource.ResourceContainerResourceHandlerAdapter;

import net.minecraft.world.level.material.Fluids;

/**
 * Check for the container max stack size instead of the interface export limit
 */
public class ExpandedResourceContainerResourceHandlerAdapter extends ResourceContainerResourceHandlerAdapter {
    private final ResourceContainer container;

    public ExpandedResourceContainerResourceHandlerAdapter(final ResourceContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public long getCapacityAsLong(final int index, final net.neoforged.neoforge.transfer.fluid.FluidResource resource) {
        final ResourceKey containerResource = this.container.getResource(index);
        if (containerResource == null || containerResource instanceof FluidResource) {
            return (int) Math.max(this.container.getMaxAmount(containerResource == null ? new FluidResource(Fluids.EMPTY) : containerResource),
                ResourceTypes.FLUID.getInterfaceExportLimit());
        }
        return 0L;
    }
}
