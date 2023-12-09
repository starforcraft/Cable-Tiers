package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.TieredNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TieredBlockEntity<N extends TieredNetworkNode<N>> extends NetworkNodeBlockEntity<N> {
    private final ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType;
    private final CableTier tier;

    protected TieredBlockEntity(ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType, CableTier tier, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec specs) {
        super(contentType.getBlockEntityType(tier), pos, state, specs, contentType.getNetworkNodeClass(tier));
        this.contentType = contentType;
        this.tier        = tier;
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
