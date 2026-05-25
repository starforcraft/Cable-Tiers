package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntityRenderer;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.neoforge.support.render.RenderTypes;

import org.jspecify.annotations.Nullable;

public class TieredDiskInterfaceBlockEntityRenderer<T extends AbstractTieredDiskInterfaceBlockEntity>
    extends AbstractTieredDiskInterfaceBlockEntityRenderer<T> {
    public TieredDiskInterfaceBlockEntityRenderer() {
        super(RenderTypes.DISK_LEDS);
    }

    @Override
    protected Disk @Nullable [] extractDisks(final T blockEntity) {
        return blockEntity.getModelData().get(ForgeTieredDiskInterfaceBlockEntity.DISKS_PROPERTY);
    }
}
