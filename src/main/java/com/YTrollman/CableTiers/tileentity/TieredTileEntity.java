package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TieredTileEntity<N extends NetworkNode> extends NetworkNodeTile<N> {

    private final ContentType<?, ?, ?, N> type;
    private final CableTier tier;

    protected TieredTileEntity(ContentType<?, ?, ?, N> type, CableTier tier) {
        super(type.getTileEntityType(tier));
        this.type = type;
        this.tier = tier;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public N createNode(World world, BlockPos pos) {
        return type.createNetworkNode(world, pos, tier);
    }
}
