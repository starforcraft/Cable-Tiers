package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntityRenderer;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.neoforge.support.render.RenderTypes;

public class TieredDiskInterfaceBlockEntityRendererImpl<T extends AbstractTieredDiskInterfaceBlockEntity>
    extends AbstractTieredDiskInterfaceBlockEntityRenderer<T> {
    public TieredDiskInterfaceBlockEntityRendererImpl() {
        super(RenderTypes.DISK_LED);
    }

    @Override
    protected Disk[] getDisks(final AbstractTieredDiskInterfaceBlockEntity blockEntity) {
        return blockEntity.getModelData().get(ForgeTieredDiskInterfaceBlockEntity.DISKS_PROPERTY);
    }
}
