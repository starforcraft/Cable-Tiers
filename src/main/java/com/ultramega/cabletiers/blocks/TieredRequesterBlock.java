package com.ultramega.cabletiers.blocks;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredRequesterBlockEntity;
import com.ultramega.cabletiers.node.TieredRequesterNetworkNode;
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
