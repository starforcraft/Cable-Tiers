package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.tileentity.TieredTileEntity;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TieredNetworkNode<N extends TieredNetworkNode<N>> extends NetworkNode {

    private final ContentType<?, ? extends TieredTileEntity<N>, ?, N> contentType;
    private final CableTier tier;
    private final ResourceLocation id;

    protected TieredNetworkNode(World world, BlockPos pos, ContentType<?, ? extends TieredTileEntity<N>, ?, N> contentType, CableTier tier) {
        super(world, pos);
        this.contentType = contentType;
        this.tier = tier;
        this.id = contentType.getId(tier);
    }

    public ContentType<?, ? extends TieredTileEntity<N>, ?, N> getContentType() {
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
