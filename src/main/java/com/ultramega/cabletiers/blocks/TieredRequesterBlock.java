package com.ultramega.cabletiers.blocks;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredRequesterBlockEntity;
import com.ultramega.cabletiers.node.TieredRequesterNetworkNode;

public class TieredRequesterBlock extends TieredNetworkBlock<TieredRequesterBlockEntity, TieredRequesterNetworkNode> {
    public TieredRequesterBlock(CableTier tier) {
        super(ContentType.REQUESTER, tier);
    }
}
