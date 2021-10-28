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

package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.config.CableConfig;
import com.YTrollman.CableTiers.tileentity.TieredRequesterTileEntity;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TieredRequesterNetworkNode extends TieredNetworkNode<TieredRequesterNetworkNode> implements IType {

    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilter";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_MISSING = "MissingItems";

    private BaseItemHandler itemFilter = new BaseItemHandler(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeInventoryListener(this));
    private FluidInventory fluidFilter = new FluidInventory(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeFluidInventoryListener(this));

    private int type = IType.ITEMS;
    private int amount = 0;
    private int slot = 0;
    private boolean isMissingItems = false;
    private int attemptAmount = getTierMaxCraftAmount();
    private ICraftingTask craftingTask = null;

    public TieredRequesterNetworkNode(World world, BlockPos pos, CableTier tier) {
        super(world, pos, ContentType.REQUESTER, tier);
    }

    @Override
    public int getEnergyUsage() {
        if(getTier() == CableTier.ELITE)
        {
            return 10 * CableConfig.ELITE_ENERGY_COST.get();
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return 10 * CableConfig.ULTRA_ENERGY_COST.get();
        }
        else if(getTier() == CableTier.CREATIVE)
        {
            return 10 * CableConfig.CREATIVE_ENERGY_COST.get();
        }
        return 0;
    }

    @Override
    public void update() {
        super.update();
        if (network == null) return;
        if(!canUpdate()) return;

        if(!(getTier() == CableTier.CREATIVE))
        {
            if(ticks % 70 == 0)
            {
                return;
            }
        }

        if (craftingTask == null || !network.getCraftingManager().getTasks().contains(craftingTask)) {
            if (type == IType.ITEMS) {
                while (slot < itemFilter.getSlots() && itemFilter.getStackInSlot(slot).isEmpty()) {
                    ++slot;
                }
                if (slot >= itemFilter.getSlots()) {
                    slot = 0;
                }
                ItemStack filter = itemFilter.getStackInSlot(slot);
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
                if (slot >= itemFilter.getSlots()) {
                    slot = 0;
                }
            }
            if (type == IType.FLUIDS) {
                while (slot < fluidFilter.getSlots() && fluidFilter.getFluid(slot).isEmpty()) {
                    ++slot;
                }
                if (slot >= fluidFilter.getSlots()) {
                    slot = 0;
                }
                FluidStack filter = fluidFilter.getFluid(slot);
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
                if (slot >= fluidFilter.getSlots()) {
                    slot = 0;
                }
            }
        }
    }

    private int getTierMaxCraftAmount()
    {
        if(getTier() == CableTier.CREATIVE)
        {
            return Integer.MAX_VALUE;
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return CableConfig.ULTRA_REQUESTER_MAX_CRAFT_AMOUNT.get();
        }
        else if(getTier() == CableTier.ELITE)
        {
            return CableConfig.ELITE_REQUESTER_MAX_CRAFT_AMOUNT.get();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int getType() {
        return world.isClientSide ? TieredRequesterTileEntity.TYPE.getValue() : type;
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
        return itemFilter;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilter;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        StackUtils.readItems(itemFilter, 0, tag);
        if (tag.contains(NBT_AMOUNT)) {
            amount = tag.getInt(NBT_AMOUNT);
        }
        if (tag.contains(NBT_MISSING)) {
            isMissingItems = tag.getBoolean(NBT_MISSING);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        StackUtils.writeItems(itemFilter, 0, tag);
        tag.putInt(NBT_AMOUNT, amount);
        tag.putBoolean(NBT_MISSING, isMissingItems);
        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);
        tag.putInt(NBT_TYPE, type);
        StackUtils.writeItems(itemFilter, 0, tag);
        tag.put(NBT_FLUID_FILTERS, fluidFilter.writeToNbt());
        tag.putInt(NBT_AMOUNT, amount);
        tag.putBoolean(NBT_MISSING, isMissingItems);
        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }
        StackUtils.readItems(itemFilter, 0, tag);
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilter.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
        if (tag.contains(NBT_AMOUNT)) {
            amount = tag.getInt(NBT_AMOUNT);
        }
        if (tag.contains(NBT_MISSING)) {
            isMissingItems = tag.getBoolean(NBT_MISSING);
        }
    }
}
