package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredBlockEntity;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class TieredNetworkNode<N extends TieredNetworkNode<N>> extends NetworkNode {
    private final ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType;
    private final CableTier tier;
    private final ResourceLocation id;

    protected TieredNetworkNode(Level level, BlockPos pos, ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType, CableTier tier) {
        super(level, pos);
        this.contentType = contentType;
        this.tier = tier;
        this.id = contentType.getId(tier);
    }

    public ContentType<?, ? extends TieredBlockEntity<N>, ?, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
