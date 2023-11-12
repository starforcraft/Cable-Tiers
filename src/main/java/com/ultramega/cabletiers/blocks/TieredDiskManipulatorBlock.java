package com.ultramega.cabletiers.blocks;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredDiskManipulatorBlockEntity;
import com.ultramega.cabletiers.node.TieredDiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.block.BlockDirection;

public class TieredDiskManipulatorBlock extends TieredNetworkBlock<TieredDiskManipulatorBlockEntity, TieredDiskManipulatorNetworkNode> {

    public TieredDiskManipulatorBlock(CableTier tier) {
        super(ContentType.DISK_MANIPULATOR, tier);
    }

    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
