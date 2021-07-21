package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredImporterTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import net.minecraft.entity.player.PlayerEntity;

public class TieredImporterContainer extends TieredContainer<TieredImporterTileEntity, TieredImporterNetworkNode> {

    public TieredImporterContainer(int windowId, PlayerEntity player, TieredImporterTileEntity tile) {
        super(ContentType.IMPORTER, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(getTier().getSlots(), 9));

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
