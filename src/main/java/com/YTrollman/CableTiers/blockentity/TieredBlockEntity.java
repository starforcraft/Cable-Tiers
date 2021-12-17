package com.YTrollman.CableTiers.blockentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class TieredBlockEntity<N extends TieredNetworkNode<N>> extends NetworkNodeBlockEntity<N> {

    private final ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType;
    private final CableTier tier;

    protected TieredBlockEntity(ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType, CableTier tier) {
        super(contentType.getBlockEntityType(tier), contentType.getBlock(tier), contentType.getBlock(tier).defaultBlockState());
        this.contentType = contentType;
        this.tier = tier;
    }

    public ContentType<?, ? extends TieredBlockEntity<N>, ?, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public N createNode(Level level, BlockPos pos) {
        return contentType.createNetworkNode(level, pos, tier);
    }
}
