package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TieredConstructorTileEntity extends NetworkNodeTile<TieredConstructorNetworkNode> {

    public static final TileDataParameter<Integer, TieredConstructorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredConstructorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, TieredConstructorTileEntity> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    private final CableTier tier;

    public TieredConstructorTileEntity(CableTier tier) {
        super(ContentType.CONSTRUCTOR.getTileEntityType(tier));
        this.tier = tier;
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    @Nonnull
    public TieredConstructorNetworkNode createNode(World world, BlockPos pos) {
        return ContentType.CONSTRUCTOR.createNetworkNode(world, pos, tier);
    }
}
