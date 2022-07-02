package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredConstructorTileEntity;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;

public class TieredConstructorContainer extends TieredContainer<TieredConstructorTileEntity, TieredConstructorNetworkNode> {

    public TieredConstructorContainer(int windowId, PlayerEntity player, TieredConstructorTileEntity tile) {
        super(ContentType.CONSTRUCTOR, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());

        for (int i = 0; i < getTier().getSlotsMultiplier(); i++) {
            int x = (35 + (18 * checkTier())) + (18 * i);

            addSlot(new FilterSlot(getNode().getItemFilters(), i, x, 20).setEnableHandler(() -> getNode().getType() == IType.ITEMS));
            addSlot(new FluidFilterSlot(getNode().getFluidFilters(), i, x, 20, i).setEnableHandler(() -> getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }

    private int checkTier() {
        if (getTier() == CableTier.CREATIVE) {
            return 0;
        } else if (getTier() == CableTier.ULTRA) {
            return 1;
        } else if (getTier() == CableTier.ELITE) {
            return 2;
        }
        return 0;
    }
}
