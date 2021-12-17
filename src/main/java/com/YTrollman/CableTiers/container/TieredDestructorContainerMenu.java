package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredDestructorBlockEntity;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.YTrollman.CableTiers.util.MathUtil;
import net.minecraft.world.entity.player.Player;

public class TieredDestructorContainerMenu extends TieredContainerMenu<TieredDestructorBlockEntity, TieredDestructorNetworkNode> {

    public TieredDestructorContainerMenu(int windowId, Player player, TieredDestructorBlockEntity tile) {
        super(ContentType.DESTRUCTOR, tile, player, windowId);
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
