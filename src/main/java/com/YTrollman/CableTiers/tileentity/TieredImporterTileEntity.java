package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TieredImporterTileEntity extends NetworkNodeTile<TieredImporterNetworkNode> {

    public static final TileDataParameter<Integer, TieredImporterTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredImporterTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TieredImporterTileEntity> TYPE = IType.createParameter();

    private final CableTier tier;

    public TieredImporterTileEntity(CableTier tier) {
        super(ContentType.IMPORTER.getTileEntityType(tier));
        this.tier = tier;

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public TieredImporterNetworkNode createNode(World world, BlockPos pos) {
        return ContentType.IMPORTER.createNetworkNode(world, pos, tier);
    }
}
