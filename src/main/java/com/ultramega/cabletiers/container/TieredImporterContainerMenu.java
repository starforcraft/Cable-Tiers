package com.ultramega.cabletiers.container;

import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredImporterBlockEntity;
import com.ultramega.cabletiers.node.TieredImporterNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
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
