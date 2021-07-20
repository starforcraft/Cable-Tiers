package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.config.CableConfig;
import com.YTrollman.CableTiers.tileentity.TieredImporterTileEntity;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
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

public class TieredImporterNetworkNode extends NetworkNode implements IComparable, IWhitelistBlacklist, IType {

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private final CableTier tier;
    private final ResourceLocation id;

    private final BaseItemHandler itemFilters;
    private final FluidInventory fluidFilters;

    private final UpgradeItemHandler upgrades;

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;

    private int currentSlot;

    public TieredImporterNetworkNode(World world, BlockPos pos, CableTier tier) {
        super(world, pos);
        this.tier = tier;
        this.id = ContentType.IMPORTER.getId(tier);

        itemFilters = new BaseItemHandler(tier.getSlots()).addListener(new NetworkNodeInventoryListener(this));
        fluidFilters = new FluidInventory(tier.getSlots()).addListener(new NetworkNodeFluidInventoryListener(this));
        upgrades = (UpgradeItemHandler) new UpgradeItemHandler(
                tier == CableTier.CREATIVE ? 0 : 4,
                tier == CableTier.ELITE ? new UpgradeItem.Type[] { UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK } : new UpgradeItem.Type[] { UpgradeItem.Type.SPEED }
        ).addListener(new NetworkNodeInventoryListener(this));
    }

    @Override
    public int getEnergyUsage() {
        // TODO: change energy cost for higher tiers (wasn't implemented before)
        return 4 * (RS.SERVER_CONFIG.getImporter().getUsage() + upgrades.getEnergyUsage());
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate() || !world.isLoaded(pos)) {
            return;
        }

        if (tier != CableTier.CREATIVE && ticks % upgrades.getSpeed() != 0) {
            return;
        }

        if (type == IType.ITEMS) {
            itemUpdate();
        } else if (type == IType.FLUIDS) {
            fluidUpdate();
        }
    }

    private int getSpeedMultiplier() {
        switch (tier) {
            case ELITE:
                return CableConfig.ELITE_IMPORTER_SPEED.get();
            case ULTRA:
                return CableConfig.ULTRA_IMPORTER_SPEED.get();
            default:
                throw new RuntimeException("illegal tier " + tier);
        }
    }

    private boolean interactWithStacks() {
        return tier != CableTier.ELITE || upgrades.hasUpgrade(UpgradeItem.Type.STACK);
    }

    private void itemUpdate() {
        TileEntity facing = getFacingTile();
        if (facing == null || facing instanceof DiskDriveTile) {
            return;
        }

        IItemHandler handler = WorldUtils.getItemHandler(facing, getDirection().getOpposite());
        if (handler == null || handler.getSlots() <= 0) {
            return;
        }

        if (currentSlot >= handler.getSlots()) {
            currentSlot = 0;
        }

        if (tier == CableTier.CREATIVE) {
            while (doItemExtraction(handler)) {
            }
        } else {
            int speedMultiplier = getSpeedMultiplier();
            for (int i = 0; i < speedMultiplier; i++) {
                if (!doItemExtraction(handler)) {
                    return;
                }
            }
        }
    }

    private boolean doItemExtraction(IItemHandler handler) {
        int startSlot = currentSlot;

        while (true) {
            ItemStack stack = handler.getStackInSlot(currentSlot);
            if (!stack.isEmpty() && IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, stack)) {
                int interactionCount = interactWithStacks() ? stack.getCount() : 1;

                ItemStack result = handler.extractItem(currentSlot, interactionCount, true);
                if (!result.isEmpty()) {
                    int remaining = network.insertItem(result, result.getCount(), Action.SIMULATE).getCount();
                    int inserted = interactionCount - remaining;
                    if (inserted > 0) {
                        result = handler.extractItem(currentSlot, inserted, false);
                        ItemStack actualRemainder = network.insertItemTracked(result, result.getCount());
                        if (!actualRemainder.isEmpty()) {
                            throw new RuntimeException("could not insert extracted item stack into network");
                        }

                        return true;
                    }
                }
            }

            currentSlot++;
            currentSlot %= handler.getSlots();
            if (currentSlot == startSlot) {
                return false;
            }
        }
    }

    private void fluidUpdate() {
        IFluidHandler handler = WorldUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());
        if (handler == null) {
            return;
        }

        if (currentSlot >= handler.getTanks()) {
            currentSlot = 0;
        }

        if (tier == CableTier.CREATIVE) {
            while (doFluidExtraction(handler)) {
            }
        } else {
            int speedMultiplier = getSpeedMultiplier();
            for (int i = 0; i < speedMultiplier; i++) {
                if (!doFluidExtraction(handler)) {
                    return;
                }
            }
        }
    }

    private boolean doFluidExtraction(IFluidHandler handler) {
        int startSlot = currentSlot;

        while (true) {
            FluidStack stack = handler.getFluidInTank(currentSlot);
            if (!stack.isEmpty() && IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack)) {
                int interactionAmount = interactWithStacks() ? stack.getAmount() : FluidAttributes.BUCKET_VOLUME;
                FluidStack toExtract = stack.copy();
                toExtract.setAmount(interactionAmount);

                FluidStack result = handler.drain(toExtract, IFluidHandler.FluidAction.SIMULATE);
                if (!result.isEmpty()) {
                    int remaining = network.insertFluid(result, result.getAmount(), Action.SIMULATE).getAmount();
                    int inserted = interactionAmount - remaining;
                    if (inserted > 0) {
                        toExtract = stack.copy();
                        toExtract.setAmount(inserted);

                        result = handler.drain(toExtract, IFluidHandler.FluidAction.EXECUTE);
                        FluidStack actualRemainder = network.insertFluidTracked(result, result.getAmount());
                        if (!actualRemainder.isEmpty()) {
                            throw new RuntimeException("could not insert extracted fluid stack into network");
                        }

                        return true;
                    }
                }
            }

            currentSlot++;
            currentSlot %= handler.getTanks();
            if (currentSlot == startSlot) {
                return false;
            }
        }
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;
        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;
        markDirty();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        StackUtils.writeItems(upgrades, 1, tag);
        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        StackUtils.writeItems(itemFilters, 0, tag);
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
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

    public UpgradeItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public int getType() {
        return world.isClientSide ? TieredImporterTileEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }
}
