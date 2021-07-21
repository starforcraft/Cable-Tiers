package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredConstructorTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class TieredConstructorContainer extends BaseContainer {

    private final CableTier tier;

    public TieredConstructorContainer(int windowId, PlayerEntity player, TieredConstructorTileEntity tile, CableTier tier) {
        super(ContentType.CONSTRUCTOR.getContainerType(tier), tile, player, windowId);
        this.tier = tier;
        initSlots();
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public TieredConstructorTileEntity getTile() {
        return (TieredConstructorTileEntity) super.getTile();
    }

    private TieredConstructorNetworkNode getNode() {
        return getTile().getNode();
    }

    private void initSlots() {
        for (int i = 0; i < getNode().getUpgrades().getSlots(); i++) {
            addSlot(new SlotItemHandler(getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlot(new FilterSlot(
                getNode().getItemFilters(),
                0, 80, 20
        ).setEnableHandler(() -> getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(
                getNode().getFluidFilters(),
                0, 80, 20, 0
        ).setEnableHandler(() -> getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
