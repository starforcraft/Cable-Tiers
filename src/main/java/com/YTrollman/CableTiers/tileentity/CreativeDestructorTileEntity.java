package com.YTrollman.CableTiers.tileentity;

import javax.annotation.Nonnull;

import com.YTrollman.CableTiers.node.CreativeDestructorNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeDestructorTileEntity extends NetworkNodeTile<CreativeDestructorNetworkNode> {
    public static final TileDataParameter<Integer, CreativeDestructorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, CreativeDestructorTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, CreativeDestructorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, CreativeDestructorTileEntity> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public CreativeDestructorTileEntity() {
        super(ModTileEntityTypes.CREATIVE_DESTRUCTOR_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    @Nonnull
    public CreativeDestructorNetworkNode createNode(World world, BlockPos pos) {
        return new CreativeDestructorNetworkNode(world, pos);
    }
}
