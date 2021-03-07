package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.EliteImporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EliteImporterTileEntity extends NetworkNodeTile<EliteImporterNetworkNode> {
    public static final TileDataParameter<Integer, EliteImporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, EliteImporterTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, EliteImporterTileEntity> TYPE = IType.createParameter();

    public EliteImporterTileEntity()
    {
        super(ModTileEntityTypes.ELITE_IMPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public EliteImporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new EliteImporterNetworkNode(world, blockPos);
    }
}
