package com.ultramega.cabletiers.fabric.storage.diskinterface;

import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntityRenderer;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.fabric.support.render.RenderTypes;

import javax.annotation.Nullable;

public class TieredDiskInterfaceBlockEntityRendererImpl<T extends AbstractTieredDiskInterfaceBlockEntity>
    extends AbstractTieredDiskInterfaceBlockEntityRenderer<T> {
    public TieredDiskInterfaceBlockEntityRendererImpl() {
        super(RenderTypes.DISK_LED);
    }

    @Override
    @Nullable
    protected Disk[] getDisks(final AbstractTieredDiskInterfaceBlockEntity blockEntity) {
        if (!(blockEntity instanceof FabricTieredDiskInterfaceBlockEntity fabricBlockEntity)) {
            return null;
        }
        if (fabricBlockEntity.getRenderData() instanceof Disk[] disks) {
            return disks;
        }
        return null;
    }
}
