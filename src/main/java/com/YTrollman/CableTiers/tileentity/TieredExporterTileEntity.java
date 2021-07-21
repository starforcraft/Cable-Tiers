package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredExporterNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

public class TieredExporterTileEntity extends TieredTileEntity<TieredExporterNetworkNode> {

    public static final TileDataParameter<Integer, TieredExporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredExporterTileEntity> TYPE = IType.createParameter();

    public TieredExporterTileEntity(CableTier tier) {
        super(ContentType.EXPORTER, tier);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }
}
