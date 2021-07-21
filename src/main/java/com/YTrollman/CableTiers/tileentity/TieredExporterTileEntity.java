package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredExporterNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TieredExporterTileEntity extends NetworkNodeTile<TieredExporterNetworkNode> {

    public static final TileDataParameter<Integer, TieredExporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredExporterTileEntity> TYPE = IType.createParameter();

    private final CableTier tier;

    public TieredExporterTileEntity(CableTier tier) {
        super(ContentType.IMPORTER.getTileEntityType(tier));
        this.tier = tier;
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public TieredExporterNetworkNode createNode(World world, BlockPos pos) {
        return ContentType.EXPORTER.createNetworkNode(world, pos, tier);
    }
}
