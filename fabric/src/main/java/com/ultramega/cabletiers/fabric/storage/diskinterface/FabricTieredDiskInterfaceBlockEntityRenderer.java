package com.ultramega.cabletiers.fabric.storage.diskinterface;

import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntityRenderer;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.fabric.support.render.RenderTypes;

import org.jspecify.annotations.Nullable;

public class FabricTieredDiskInterfaceBlockEntityRenderer<T extends AbstractTieredDiskInterfaceBlockEntity>
    extends AbstractTieredDiskInterfaceBlockEntityRenderer<T> {
    public FabricTieredDiskInterfaceBlockEntityRenderer() {
        super(RenderTypes.DISK_LEDS);
    }

    @Override
    protected Disk @Nullable [] extractDisks(final AbstractTieredDiskInterfaceBlockEntity blockEntity) {
        if (!(blockEntity instanceof FabricTieredDiskInterfaceBlockEntity fabricBlockEntity)) {
            return null;
        }
        if (fabricBlockEntity.getRenderData() instanceof Disk[] disks) {
            return disks;
        }
        return null;
    }
}
