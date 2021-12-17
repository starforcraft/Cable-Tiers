package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.YTrollman.CableTiers.blockentity.TieredBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TieredContainerMenu<T extends TieredBlockEntity<N>, N extends TieredNetworkNode<N>> extends BaseContainerMenu implements Container {

    private final ContentType<?, T, ? extends TieredContainerMenu<T, N>, N> contentType;

    protected TieredContainerMenu(ContentType<?, T, ? extends TieredContainerMenu<T, N>, N> contentType, T tile, Player player, int windowId) {
        super(contentType.getContainerType(tile.getTier()), tile, player, windowId);
        this.contentType = contentType;
    }

    public ContentType<?, T, ? extends TieredContainerMenu<T, N>, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return getBlockEntity().getTier();
    }

    @Override
    public T getBlockEntity() {
        return (T) super.getBlockEntity();
    }

    public N getNode() {
        return getBlockEntity().getNode();
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

    public int getContainerSize() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public ItemStack getItem(int p_18941_) {
        return null;
    }

    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return null;
    }

    public ItemStack removeItemNoUpdate(int p_18951_) {
        return null;
    }

    public void setItem(int p_18944_, ItemStack p_18945_) {

    }

    public void setChanged() {

    }

    public void clearContent() {

    }
}
