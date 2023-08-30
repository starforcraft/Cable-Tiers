package com.ultramega.cabletiers.blocks;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredInterfaceBlockEntity;
import com.ultramega.cabletiers.node.TieredInterfaceNetworkNode;

public class TieredInterfaceBlock extends TieredNetworkBlock<TieredInterfaceBlockEntity, TieredInterfaceNetworkNode> {
    public TieredInterfaceBlock(CableTier tier) {
        super(ContentType.INTERFACE, tier);
    }
}
