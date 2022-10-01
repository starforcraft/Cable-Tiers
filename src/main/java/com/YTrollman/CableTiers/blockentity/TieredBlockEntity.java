package com.YTrollman.CableTiers.blockentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TieredBlockEntity<N extends TieredNetworkNode<N>> extends NetworkNodeBlockEntity<N> {
    private final ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType;
    private final CableTier tier;

    protected TieredBlockEntity(ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType, CableTier tier, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec specs) {
        super(contentType.getBlockEntityType(tier), pos, state, specs);
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
