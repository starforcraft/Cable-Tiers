package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.CreativeImporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeImporterTileEntity extends NetworkNodeTile<CreativeImporterNetworkNode> {
    public static final TileDataParameter<Integer, CreativeImporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, CreativeImporterTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, CreativeImporterTileEntity> TYPE = IType.createParameter();

    public CreativeImporterTileEntity()
    {
        super(ModTileEntityTypes.CREATIVE_IMPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public CreativeImporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new CreativeImporterNetworkNode(world, blockPos);
    }
}
