package com.ultramega.cabletiers.neoforge.capability;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/**
 * Check for the slot max stack size instead of the stack max stack size
 */
public class ImprovedInvWrapper extends InvWrapper {
    public ImprovedInvWrapper(final Container inv) {
        super(inv);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            final ItemStack stackInSlot = this.getInv().getItem(slot);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getCount() >= Math.max(stackInSlot.getMaxStackSize(), this.getSlotLimit(slot))) {
                    return stack;
                } else if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                    return stack;
                } else if (!this.getInv().canPlaceItem(slot, stack)) {
                    return stack;
                } else {
                    final int m = Math.max(stack.getMaxStackSize(), this.getSlotLimit(slot)) - stackInSlot.getCount();
                    if (stack.getCount() <= m) {
                        if (!simulate) {
                            final ItemStack copy = stack.copy();
                            copy.grow(stackInSlot.getCount());
                            this.getInv().setItem(slot, copy);
                            this.getInv().setChanged();
                        }

                        return ItemStack.EMPTY;
                    } else {
                        final ItemStack copyStack = stack.copy();
                        if (!simulate) {
                            final ItemStack copy = copyStack.split(m);
                            copy.grow(stackInSlot.getCount());
                            this.getInv().setItem(slot, copy);
                            this.getInv().setChanged();
                            return copyStack;
                        } else {
                            copyStack.shrink(m);
                            return copyStack;
                        }
                    }
                }
            } else if (!this.getInv().canPlaceItem(slot, stack)) {
                return stack;
            } else {
                final int m = Math.max(stack.getMaxStackSize(), this.getSlotLimit(slot));
                if (m < stack.getCount()) {
                    final ItemStack copyStack = stack.copy();
                    if (!simulate) {
                        this.getInv().setItem(slot, copyStack.split(m));
                        this.getInv().setChanged();
                        return copyStack;
                    } else {
                        copyStack.shrink(m);
                        return copyStack;
                    }
                } else {
                    if (!simulate) {
                        this.getInv().setItem(slot, stack);
                        this.getInv().setChanged();
                    }

                    return ItemStack.EMPTY;
                }
            }
        }
    }
}
