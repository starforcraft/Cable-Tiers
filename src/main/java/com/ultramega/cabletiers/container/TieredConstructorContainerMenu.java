package com.ultramega.cabletiers.container;

import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredConstructorBlockEntity;
import com.ultramega.cabletiers.node.TieredConstructorNetworkNode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TieredConstructorContainerMenu extends TieredContainerMenu<TieredConstructorBlockEntity, TieredConstructorNetworkNode> {
    public TieredConstructorContainerMenu(int windowId, Player player, TieredConstructorBlockEntity tile) {
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

        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }

    private int checkTier() {
        return switch (getTier()) {
            case ELITE -> 2;
            case ULTRA -> 1;
            case MEGA -> 0;
        };
    }
}
