package com.YTrollman.CableTiers.tileentity;

import javax.annotation.Nonnull;

import com.YTrollman.CableTiers.node.EliteConstructorNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EliteConstructorTileEntity extends NetworkNodeTile<EliteConstructorNetworkNode> {
    public static final TileDataParameter<Integer, EliteConstructorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, EliteConstructorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, EliteConstructorTileEntity> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public EliteConstructorTileEntity() {
        super(ModTileEntityTypes.ELITE_CONSTRUCTOR_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    @Nonnull
    public EliteConstructorNetworkNode createNode(World world, BlockPos pos) {
        return new EliteConstructorNetworkNode(world, pos);
    }
}
