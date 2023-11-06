package com.ultramega.cabletiers.container;

import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredRequesterBlockEntity;
import com.ultramega.cabletiers.node.TieredRequesterNetworkNode;
import net.minecraft.world.entity.player.Player;

public class TieredRequesterContainerMenu extends TieredContainerMenu<TieredRequesterBlockEntity, TieredRequesterNetworkNode> {
    public TieredRequesterContainerMenu(int windowId, Player player, TieredRequesterBlockEntity tile) {
        super(ContentType.REQUESTER, tile, player, windowId);

        initSlots();
    }

    private void initSlots() {
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 55 + (18 * (getTier().getSlotsMultiplier() - 1)));

        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
