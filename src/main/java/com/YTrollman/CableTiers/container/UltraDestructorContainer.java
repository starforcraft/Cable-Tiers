package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.registry.ModContainers;
import com.YTrollman.CableTiers.tileentity.UltraDestructorTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.config.IType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class UltraDestructorContainer extends BaseContainer {
    public UltraDestructorContainer(UltraDestructorTileEntity destructor, PlayerEntity player, int windowId) {
        super(ModContainers.ULTRA_DESTRUCTOR_CONTAINER.get(), destructor, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(destructor.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                int index = (i * 9) + j;
                int x = 8 + (18 * j);
                int y = 20 + (18 * i);
                
                addSlot(new FilterSlot(
                		destructor.getNode().getItemFilters(), 
                		index, x, y
                ).setEnableHandler(() -> destructor.getNode().getType() == IType.ITEMS));
                
                addSlot(new FluidFilterSlot(
                		destructor.getNode().getFluidFilters(), index, x, y
                ).setEnableHandler(() -> destructor.getNode().getType() == IType.FLUIDS));
            }
        }

        addPlayerInventory(8, 109);

        transferManager.addBiTransfer(player.inventory, destructor.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, destructor.getNode().getItemFilters(), destructor.getNode().getFluidFilters(), destructor.getNode()::getType);
    }
}
