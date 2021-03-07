package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.registry.ModContainers;
import com.YTrollman.CableTiers.tileentity.CreativeExporterTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.config.IType;

import net.minecraft.entity.player.PlayerEntity;

public class CreativeExporterContainer extends BaseContainer {
    private final CreativeExporterTileEntity tile;
    private boolean hasRegulatorMode;

    public CreativeExporterContainer(int windowId, PlayerEntity player, CreativeExporterTileEntity tile)
    {
        super(ModContainers.CREATIVE_EXPORTER_CONTAINER.get(), tile, player, windowId);
        this.tile = tile;
        this.hasRegulatorMode = hasRegulatorMode();
        initSlots();
    }

    private boolean hasRegulatorMode()
    {
        return tile.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode)
        {
            hasRegulatorMode = updatedHasRegulatorMode;
            initSlots();
        }
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        this.transferManager.clearTransfers();

        boolean hasRegulator = tile.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);

        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                int index = (i * 9) + j;
                int x = 8 + (18 * j);
                int y = 20 + (18 * i);

                addSlot(new FilterSlot(
                        tile.getNode().getItemFilters(),
                        index, x, y,
                        hasRegulator ? FilterSlot.FILTER_ALLOW_SIZE : 0
                ).setEnableHandler(() -> tile.getNode().getType() == IType.ITEMS));

                addSlot(new FluidFilterSlot(
                        tile.getNode().getFluidFilters(),
                        index, x, y,
                        hasRegulator ? FluidFilterSlot.FILTER_ALLOW_SIZE : 0
                ).setEnableHandler(() -> tile.getNode().getType() == IType.FLUIDS));
            }
        }

        addPlayerInventory(8, 145);

        transferManager.addBiTransfer(getPlayer().inventory, tile.getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, tile.getNode().getItemFilters(), tile.getNode().getFluidFilters(), tile.getNode()::getType);
    }

    @Override
    public CreativeExporterTileEntity getTile()
    {
        return tile;
    }
}
