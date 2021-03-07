package com.YTrollman.CableTiers.tileentity;

import javax.annotation.Nonnull;

import com.YTrollman.CableTiers.node.UltraDestructorNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UltraDestructorTileEntity extends NetworkNodeTile<UltraDestructorNetworkNode> {
    public static final TileDataParameter<Integer, UltraDestructorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, UltraDestructorTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, UltraDestructorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, UltraDestructorTileEntity> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public UltraDestructorTileEntity() {
        super(ModTileEntityTypes.ULTRA_DESTRUCTOR_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    @Nonnull
    public UltraDestructorNetworkNode createNode(World world, BlockPos pos) {
        return new UltraDestructorNetworkNode(world, pos);
    }
}
