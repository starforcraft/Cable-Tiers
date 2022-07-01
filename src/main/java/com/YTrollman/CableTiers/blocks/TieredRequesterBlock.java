package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredRequesterBlockEntity;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.refinedmods.refinedstorage.block.BlockDirection;

public class TieredRequesterBlock extends TieredNetworkBlock<TieredRequesterBlockEntity, TieredRequesterNetworkNode> {

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
