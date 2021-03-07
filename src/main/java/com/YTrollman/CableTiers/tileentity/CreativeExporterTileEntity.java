package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.node.CreativeExporterNetworkNode;
import com.YTrollman.CableTiers.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeExporterTileEntity extends NetworkNodeTile<CreativeExporterNetworkNode> {
    public static final TileDataParameter<Integer, CreativeExporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, CreativeExporterTileEntity> TYPE = IType.createParameter();

    public CreativeExporterTileEntity()
    {
        super(ModTileEntityTypes.CREATIVE_EXPORTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public CreativeExporterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new CreativeExporterNetworkNode(world, blockPos);
    }
}
