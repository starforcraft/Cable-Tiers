package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.YTrollman.CableTiers.blockentity.TieredConstructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import net.minecraft.world.entity.player.Player;

public class TieredConstructorContainerMenu extends TieredContainerMenu<TieredConstructorBlockEntity, TieredConstructorNetworkNode> {

    public TieredConstructorContainerMenu(int windowId, Player player, TieredConstructorBlockEntity tile) {
        super(ContentType.CONSTRUCTOR, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());

        for(int i = 0; i < getTier().getSlotsMultiplier(); i++)
        {
            int x = (35 + (18 * checkTier())) + (18 * i);

            addSlot(new FilterSlot(
                    getNode().getItemFilters(),
                    i, x, 20
            ).setEnableHandler(() -> getNode().getType() == IType.ITEMS));
            addSlot(new FluidFilterSlot(
                    getNode().getFluidFilters(),
                    i, x, 20, i
            ).setEnableHandler(() -> getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }

    private int checkTier()
    {
        if(getTier() == CableTier.CREATIVE)
        {
            return 0;
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return 1;
        }
        else if(getTier() == CableTier.ELITE)
        {
            return 2;
        }
        return 0;
    }
}
