package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredRequesterTileEntity;
import com.refinedmods.refinedstorage.block.BlockDirection;

public class TieredRequesterBlock extends TieredNetworkBlock<TieredRequesterTileEntity, TieredRequesterNetworkNode> {

    public TieredRequesterBlock(CableTier tier) {
        super(ContentType.REQUESTER, tier);
    }

    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
