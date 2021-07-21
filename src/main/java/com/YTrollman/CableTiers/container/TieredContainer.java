package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredTileEntity;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TieredContainer<T extends TieredTileEntity<N>, N extends TieredNetworkNode<N>> extends BaseContainer {

    private final ContentType<?, T, ? extends TieredContainer<T, N>, N> contentType;

    protected TieredContainer(ContentType<?, T, ? extends TieredContainer<T, N>, N> contentType, T tile, PlayerEntity player, int windowId) {
        super(contentType.getContainerType(tile.getTier()), tile, player, windowId);
        this.contentType = contentType;
    }

    public ContentType<?, T, ? extends TieredContainer<T, N>, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return getTile().getTier();
    }

    @Override
    public T getTile() {
        return (T) super.getTile();
    }

    public N getNode() {
        return getTile().getNode();
    }

    protected void addUpgradeSlots(IItemHandler upgrades) {
        for (int i = 0; i < upgrades.getSlots(); i++) {
            addSlot(new SlotItemHandler(upgrades, i, 187, 6 + (i * 18)));
        }
    }

    protected void addFilterSlots(IItemHandler itemFilters, FluidInventory fluidFilters, IType type) {
        addFilterSlots(itemFilters, 0, fluidFilters, 0, type);
    }

    protected void addFilterSlots(IItemHandler itemFilters, int itemFlags, FluidInventory fluidFilters, int fluidFlags, IType type) {
        for (int i = 0; i < itemFilters.getSlots(); i++) {
            int x = 8 + 18 * (i % 9);
            int y = 20 + 18 * (i / 9);

            addSlot(new FilterSlot(
                    itemFilters,
                    i, x, y,
                    itemFlags
            ).setEnableHandler(() -> type.getType() == IType.ITEMS));
        }

        for (int i = 0; i < fluidFilters.getSlots(); i++) {
            int x = 8 + 18 * (i % 9);
            int y = 20 + 18 * (i / 9);

            addSlot(new FluidFilterSlot(
                    fluidFilters,
                    i, x, y,
                    fluidFlags
            ).setEnableHandler(() -> type.getType() == IType.FLUIDS));
        }
    }
}
