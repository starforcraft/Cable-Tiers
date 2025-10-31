package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;

public abstract class TieredSimpleNetworkNode extends SimpleNetworkNode {
    private CableTiers tier;
    private CableType type;

    public TieredSimpleNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    public void setTier(final CableTiers tier) {
        this.tier = tier;
    }

    public void setType(final CableType type) {
        this.type = type;
    }

    public CableTiers getTier() {
        return tier;
    }

    public CableType getType() {
        return type;
    }
}
