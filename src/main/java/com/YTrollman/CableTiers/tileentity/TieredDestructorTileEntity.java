package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TieredDestructorTileEntity extends NetworkNodeTile<TieredDestructorNetworkNode> {

    public static final TileDataParameter<Integer, TieredDestructorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredDestructorTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TieredDestructorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, TieredDestructorTileEntity> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    private final CableTier tier;

    public TieredDestructorTileEntity(CableTier tier) {
        super(ContentType.DESTRUCTOR.getTileEntityType(tier));
        this.tier = tier;

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    @Nonnull
    public TieredDestructorNetworkNode createNode(World world, BlockPos pos) {
        return ContentType.DESTRUCTOR.createNetworkNode(world, pos, tier);
    }
}
