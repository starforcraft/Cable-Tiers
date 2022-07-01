package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredDiskManipulatorBlockEntity;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
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
