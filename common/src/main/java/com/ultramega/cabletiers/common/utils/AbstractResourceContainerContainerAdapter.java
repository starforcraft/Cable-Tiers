package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Copy of {@link com.refinedmods.refinedstorage.common.support.resource.AbstractResourceContainerContainerAdapter}
 * With the difference of checking for the slot max stack size instead of the stack max stack size
 */
public abstract class AbstractResourceContainerContainerAdapter implements Container {
    private final ResourceContainer container;

    protected AbstractResourceContainerContainerAdapter(final ResourceContainer container) {
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return container.size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < container.size(); ++i) {
            if (!container.isEmpty(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(final int slotIndex) {
        return container.getStackRepresentation(slotIndex);
    }

    @Override
    public ItemStack removeItem(final int slotIndex, final int amount) {
        final ResourceKey resource = container.getResource(slotIndex);
        if (resource instanceof ItemResource itemResource) {
            final long maxRemove = Math.min(amount, container.getAmount(slotIndex));
            container.shrink(slotIndex, maxRemove);
            return itemResource.toItemStack(maxRemove);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slotIndex) {
        final ResourceKey resource = container.getResource(slotIndex);
        if (resource instanceof ItemResource itemResource) {
            final ItemStack stack = itemResource.toItemStack();
            final long maxRemove = Math.min(Math.max(container.getMaxAmount(resource), stack.getMaxStackSize()), container.getAmount(slotIndex));
            container.shrink(slotIndex, maxRemove);
            return stack.copyWithCount((int) maxRemove);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        if (container.isEmpty(slot)) {
            return true;
        }
        final PlatformResourceKey current = container.getResource(slot);
        if (!(current instanceof ItemResource)) {
            return false;
        }
        final ItemResource resource = ItemResource.ofItemStack(stack);
        return resource.equals(current)
            && container.getAmount(slot) + stack.getCount() <= Math.max(container.getMaxAmount(resource), stack.getMaxStackSize());
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        final ResourceKey resource = container.getResource(slotIndex);
        if (itemStack.isEmpty()) {
            if (resource instanceof ItemResource) {
                container.remove(slotIndex);
            }
            return;
        }
        container.set(slotIndex, new ResourceAmount(
            ItemResource.ofItemStack(itemStack),
            itemStack.getCount()
        ));
    }

    @Override
    public int getMaxStackSize() {
        return this.getMaxStackSize(ItemStack.EMPTY);
    }

    @Override
    public int getMaxStackSize(final ItemStack stack) {
        return (int) Math.clamp(0, container.getMaxAmount(ItemResource.ofItemStack(stack)), Integer.MAX_VALUE);
    }

    @Override
    public boolean stillValid(final Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < container.size(); ++i) {
            container.remove(i);
        }
    }
}
