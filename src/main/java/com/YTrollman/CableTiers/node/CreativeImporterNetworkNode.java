package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.tileentity.CreativeImporterTileEntity;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.tile.DiskDriveTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CreativeImporterNetworkNode extends NetworkNode implements IComparable, IWhitelistBlacklist, IType {
    public static final ResourceLocation ID = new ResourceLocation(CableTiers.MOD_ID, "creative_importer");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";
    
    private final BaseItemHandler itemFilters = new BaseItemHandler(54).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(54).addListener(new NetworkNodeFluidInventoryListener(this));

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(0)
        .addListener(new NetworkNodeInventoryListener(this));

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;

    private int currentSlot;
    
    public CreativeImporterNetworkNode(World world, BlockPos pos)
    {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage()
    {
        return 4 * RS.SERVER_CONFIG.getImporter().getUsage();
    }

    @Override
    public void update()
    {
        super.update();

        if (!canUpdate() || !world.isBlockPresent(pos)) {
            return;
        }

        if (type == IType.ITEMS)
        {
        	TileEntity facing = getFacingTile();
            IItemHandler handler = WorldUtils.getItemHandler(facing, getDirection().getOpposite());

            if (handler != null) {
                for(int x = 0; x < handler.getSlots(); x++) {
                	if (facing instanceof DiskDriveTile || handler == null) {
    	            	return;
    	            }
                	
                    if (currentSlot >= handler.getSlots()) {
                        currentSlot = 0;
                    }

                    if (handler.getSlots() > 0) {
                        while (currentSlot + 1 < handler.getSlots() && handler.getStackInSlot(currentSlot).isEmpty()) {
                            currentSlot++;
                        }

                        ItemStack stack = handler.getStackInSlot(currentSlot);

                        if (!IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, stack)) {
                            currentSlot++;
                        } else {
                            ItemStack result = handler.extractItem(currentSlot, 64, true);

                            if (!result.isEmpty() && network.insertItem(result, result.getCount(), Action.SIMULATE).isEmpty()) {
                                    result = handler.extractItem(currentSlot, 64, false);

                                    network.insertItemTracked(result, result.getCount());
                            } else {
                                currentSlot++;
                            }
                        }
                    }	
            	}	
            }
        }
        else if (type == IType.FLUIDS)
        {
            IFluidHandler handler = WorldUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());
            
            if (handler != null) {
                FluidStack stack = handler.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);

                if (!stack.isEmpty() &&
                        IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack) &&
                        network.insertFluid(stack, stack.getAmount(), Action.SIMULATE).isEmpty()) {
                    for(int x = 0; x < stack.getAmount(); x++) {
                        FluidStack toDrain = handler.drain(FluidAttributes.BUCKET_VOLUME * 64, IFluidHandler.FluidAction.SIMULATE);

                        if (!toDrain.isEmpty()) {
                            FluidStack remainder = network.insertFluidTracked(toDrain, toDrain.getAmount());
                            if (!remainder.isEmpty()) {
                                toDrain.shrink(remainder.getAmount());
                            }
                            handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
            	}
            }
        }
    }
    
    @Override
    public int getCompare()
    {
        return compare;
    }

    @Override
    public void setCompare(int compare)
    {
        this.compare = compare;
        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode()
    {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode)
    {
        this.mode = mode;
        markDirty();
    }

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);
        StackUtils.writeItems(upgrades, 1, tag);
        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag)
    {
        super.writeConfiguration(tag);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        StackUtils.writeItems(itemFilters, 0, tag);
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        return tag;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);
        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public void readConfiguration(CompoundNBT tag)
    {
        super.readConfiguration(tag);
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }
        StackUtils.readItems(itemFilters, 0, tag);
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    public UpgradeItemHandler getUpgrades()
    {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops()
    {
        return upgrades;
    }

    @Override
    public int getType()
    {
        return world.isRemote ? CreativeImporterTileEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type)
    {
        this.type = type;
        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters()
    {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters()
    {
        return fluidFilters;
    }
}
