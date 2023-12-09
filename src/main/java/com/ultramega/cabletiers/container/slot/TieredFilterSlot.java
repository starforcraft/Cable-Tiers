package com.ultramega.cabletiers.container.slot;

import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.ultramega.cabletiers.CableTier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class TieredFilterSlot extends FilterSlot {
    private final CableTier tier;
    private final int inventoryIndex;

    public TieredFilterSlot(CableTier tier, IItemHandler handler, int inventoryIndex, int x, int y, int flags) {
        super(handler, inventoryIndex, x, y, flags);
        this.tier           = tier;
        this.inventoryIndex = inventoryIndex;
    }

    @Override
    public int getMaxStackSize() {
        return getTieredStackInteractCount(this.getItemHandler().getSlotLimit(this.inventoryIndex));
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = getTieredStackInteractCount(stack.getMaxStackSize());
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(inventoryIndex);
        if (handler instanceof IItemHandlerModifiable handlerModifiable) {
            handlerModifiable.setStackInSlot(inventoryIndex, ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(inventoryIndex, maxAdd, true);

            handlerModifiable.setStackInSlot(inventoryIndex, currentStack);

            return maxInput - remainder.getCount();
        } else {
            ItemStack remainder = handler.insertItem(inventoryIndex, maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    private int getTieredStackInteractCount(int maxStackSize) {
        return (int) (maxStackSize * Math.pow(tier.getSlotsMultiplier(), 3));
    }

    public CableTier getTier() {
        return tier;
    }
}
