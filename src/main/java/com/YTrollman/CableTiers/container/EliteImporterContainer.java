package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.registry.ModContainers;
import com.YTrollman.CableTiers.tileentity.EliteImporterTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.config.IType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class EliteImporterContainer extends BaseContainer {
    private final EliteImporterTileEntity tile;

    public EliteImporterContainer(int windowId, PlayerEntity player, EliteImporterTileEntity tile)
    {
        super(ModContainers.ELITE_IMPORTER_CONTAINER.get(), tile, player, windowId);
        this.tile = tile;
        initSlots();
    }

    public void initSlots() {
        for (int i = 0; i < 4; i++)
            addSlot(new SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + (i * 18)));

        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                int index = (i * 9) + j;
                int x = 8 + (18 * j);
                int y = 20 + (18 * i);

                addSlot(new FilterSlot(
                        tile.getNode().getItemFilters(),
                        index, x, y
                ).setEnableHandler(() -> tile.getNode().getType() == IType.ITEMS));

                addSlot(new FluidFilterSlot(
                        tile.getNode().getFluidFilters(),
                        index, x, y
                ).setEnableHandler(() -> tile.getNode().getType() == IType.FLUIDS));
            }
        }

        addPlayerInventory(8, 73);

        transferManager.addBiTransfer(getPlayer().inventory, tile.getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, tile.getNode().getItemFilters(), tile.getNode().getFluidFilters(), tile.getNode()::getType);
    }

    @Override
    public EliteImporterTileEntity getTile()
    {
        return tile;
    }
}
