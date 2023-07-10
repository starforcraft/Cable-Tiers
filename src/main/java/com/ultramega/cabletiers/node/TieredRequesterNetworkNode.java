/*
 * This file is part of RSRequestifyu.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ultramega.cabletiers.node;

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredRequesterBlockEntity;
import com.ultramega.cabletiers.config.CableConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TieredRequesterNetworkNode extends TieredNetworkNode<TieredRequesterNetworkNode> implements IType {
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilter";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_MISSING = "MissingItems";

    private final BaseItemHandler itemFilters = new BaseItemHandler(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeFluidInventoryListener(this));

    private int type = IType.ITEMS;
    private int amount = 0;
    private int slot = 0;
    private boolean isMissingItems = false;
    private int attemptAmount = getTierMaxCraftAmount();
    private ICraftingTask craftingTask = null;

    private final double speedMultiplier;

    public TieredRequesterNetworkNode(Level level, BlockPos pos, CableTier tier) {
        super(level, pos, ContentType.REQUESTER, tier);
        this.speedMultiplier = getSpeedMultiplier(0);
    }

    @Override
    public int getEnergyUsage() {
        return 10 * getAdditionalEnergyCost();
    }

    @Override
    public void update() {
        super.update();
        if (network == null) return;
        if (!canUpdate()) return;

        int speed = (int)Math.max(0, 70 / speedMultiplier);
        if(speed != 0) {
            if (ticks % speed != 0) {
                return;
            }
        }

        if (craftingTask == null || !network.getCraftingManager().getTasks().contains(craftingTask)) {
            if (type == IType.ITEMS) {
                while (slot < itemFilters.getSlots() && itemFilters.getStackInSlot(slot).isEmpty()) {
                    ++slot;
                }
                if (slot >= itemFilters.getSlots()) {
                    slot = 0;
                }
                ItemStack filter = itemFilters.getStackInSlot(slot);
                if (!filter.isEmpty()) {
                    ItemStack current = network.extractItem(filter, amount, Action.SIMULATE);
                    if (current == null || current.isEmpty() || current.getCount() < amount) {
                        int count = current == null || current.isEmpty() ? amount : amount - current.getCount();
                        if (count > 0) {
                            craftingTask = network.getCraftingManager().request(this, filter, Math.min(attemptAmount, count));
                            isMissingItems = true;
                        }
                    } else {
                        isMissingItems = false;
                    }
                }
                if (attemptAmount > amount) {
                    attemptAmount = amount;
                }
                if (slot == 0) {
                    if (attemptAmount <= 1) {
                        attemptAmount = getTierMaxCraftAmount();
                    } else {
                        attemptAmount /= 2;
                    }
                }
                ++slot;
                if (slot >= itemFilters.getSlots()) {
                    slot = 0;
                }
            }
            if (type == IType.FLUIDS) {
                while (slot < fluidFilters.getSlots() && fluidFilters.getFluid(slot).isEmpty()) {
                    ++slot;
                }
                if (slot >= fluidFilters.getSlots()) {
                    slot = 0;
                }
                FluidStack filter = fluidFilters.getFluid(slot);
                if (!filter.isEmpty()) {
                    FluidStack current = network.extractFluid(filter, amount, Action.SIMULATE);
                    if (current.isEmpty() || current.getAmount() < amount) {
                        int count = current.isEmpty() ? amount : amount - current.getAmount();
                        if (count > 0) {
                            craftingTask = network.getCraftingManager().request(this, filter, count);
                            isMissingItems = true;
                        }
                    } else {
                        isMissingItems = false;
                    }
                }
                if (attemptAmount > amount) {
                    attemptAmount = amount;
                }
                if (slot == 0) {
                    if (attemptAmount <= 1) {
                        attemptAmount = getTierMaxCraftAmount();
                    } else {
                        attemptAmount /= 2;
                    }
                }
                ++slot;
                if (slot >= fluidFilters.getSlots()) {
                    slot = 0;
                }
            }
        }
    }

    private int getTierMaxCraftAmount() {
        return switch (getTier()) {
            case ELITE -> CableConfig.ELITE_REQUESTER_MAX_CRAFT_AMOUNT.get();
            case ULTRA -> CableConfig.ULTRA_REQUESTER_MAX_CRAFT_AMOUNT.get();
            case MEGA -> CableConfig.MEGA_REQUESTER_MAX_CRAFT_AMOUNT.get();
        };
    }

    @Override
    public int getType() {
        return level.isClientSide ? TieredRequesterBlockEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;

        markDirty();
    }

    public boolean isMissingItems() {
        return isMissingItems && craftingTask == null;
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_AMOUNT)) {
            amount = tag.getInt(NBT_AMOUNT);
        }

        if (tag.contains(NBT_MISSING)) {
            isMissingItems = tag.getBoolean(NBT_MISSING);
        }
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.putInt(NBT_AMOUNT, amount);
        tag.putBoolean(NBT_MISSING, isMissingItems);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        tag.putInt(NBT_AMOUNT, amount);
        tag.putBoolean(NBT_MISSING, isMissingItems);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }

        if (tag.contains(NBT_AMOUNT)) {
            amount = tag.getInt(NBT_AMOUNT);
        }

        if (tag.contains(NBT_MISSING)) {
            isMissingItems = tag.getBoolean(NBT_MISSING);
        }
    }
}
