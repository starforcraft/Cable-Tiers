package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

public class TieredImporterTileEntity extends TieredTileEntity<TieredImporterNetworkNode> {

    public static final TileDataParameter<Integer, TieredImporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredImporterTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TieredImporterTileEntity> TYPE = IType.createParameter();

    public TieredImporterTileEntity(CableTier tier) {
        super(ContentType.IMPORTER, tier);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }
}
