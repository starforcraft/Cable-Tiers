package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredDestructorTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import net.minecraft.entity.player.PlayerEntity;

public class TieredDestructorContainer extends TieredContainer<TieredDestructorTileEntity, TieredDestructorNetworkNode> {

    public TieredDestructorContainer(int windowId, PlayerEntity player, TieredDestructorTileEntity tile) {
        super(ContentType.DESTRUCTOR, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());
        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9));

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
