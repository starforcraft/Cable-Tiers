package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.EliteExporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EliteExporterTileEntity extends NetworkNodeTile<EliteExporterNetworkNode> {
    public static final TileDataParameter<Integer, EliteExporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, EliteExporterTileEntity> TYPE = IType.createParameter();

    public EliteExporterTileEntity()
    {
        super(ModTileEntityTypes.ELITE_EXPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public EliteExporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new EliteExporterNetworkNode(world, blockPos);
    }
}
