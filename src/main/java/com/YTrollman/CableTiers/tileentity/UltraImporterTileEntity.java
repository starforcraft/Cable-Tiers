package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.UltraImporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UltraImporterTileEntity extends NetworkNodeTile<UltraImporterNetworkNode> {
    public static final TileDataParameter<Integer, UltraImporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, UltraImporterTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, UltraImporterTileEntity> TYPE = IType.createParameter();

    public UltraImporterTileEntity()
    {
        super(ModTileEntityTypes.ULTRA_IMPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public UltraImporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new UltraImporterNetworkNode(world, blockPos);
    }
}
