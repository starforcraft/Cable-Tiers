package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredRequesterTileEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TieredRequesterContainer extends TieredContainer<TieredRequesterTileEntity, TieredRequesterNetworkNode> {
    public TieredRequesterContainer(int windowId, PlayerEntity player, TieredRequesterTileEntity tile) {
        super(ContentType.REQUESTER, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 55 + (18 * (getTier().getSlotsMultiplier() - 1)));

        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
