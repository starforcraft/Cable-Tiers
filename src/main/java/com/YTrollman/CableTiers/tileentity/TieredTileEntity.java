package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TieredTileEntity<N extends TieredNetworkNode<N>> extends NetworkNodeTile<N> {

    private final ContentType<?, ? extends TieredTileEntity<N>, ?, N> contentType;
    private final CableTier tier;

    protected TieredTileEntity(ContentType<?, ? extends TieredTileEntity<N>, ?, N> contentType, CableTier tier) {
        super(contentType.getTileEntityType(tier));
        this.contentType = contentType;
        this.tier = tier;
    }

    public ContentType<?, ? extends TieredTileEntity<N>, ?, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public N createNode(World world, BlockPos pos) {
        return contentType.createNetworkNode(world, pos, tier);
    }
}
