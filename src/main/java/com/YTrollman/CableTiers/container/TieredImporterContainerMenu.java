package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredImporterBlockEntity;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.YTrollman.CableTiers.util.MathUtil;
import net.minecraft.world.entity.player.Player;

public class TieredImporterContainerMenu extends TieredContainerMenu<TieredImporterBlockEntity, TieredImporterNetworkNode> {

    public TieredImporterContainerMenu(int windowId, Player player, TieredImporterBlockEntity tile) {
        super(ContentType.IMPORTER, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9));

        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
