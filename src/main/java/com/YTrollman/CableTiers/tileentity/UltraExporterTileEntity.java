package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.UltraExporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UltraExporterTileEntity extends NetworkNodeTile<UltraExporterNetworkNode> {
    public static final TileDataParameter<Integer, UltraExporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, UltraExporterTileEntity> TYPE = IType.createParameter();

    public UltraExporterTileEntity()
    {
        super(ModTileEntityTypes.ULTRA_EXPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public UltraExporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new UltraExporterNetworkNode(world, blockPos);
    }
}
