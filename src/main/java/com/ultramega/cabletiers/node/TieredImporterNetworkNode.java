package com.ultramega.cabletiers.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.DiskDriveBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredImporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TieredImporterNetworkNode extends TieredNetworkNode<TieredImporterNetworkNode> implements IComparable, IWhitelistBlacklist, IType, ICoverable {
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 9;

    private final BaseItemHandler itemFilters = new BaseItemHandler(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9 * getTier().getSlotsMultiplier()).addListener(new NetworkNodeFluidInventoryListener(this));

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, getTierUpgrades()).addListener(new NetworkNodeInventoryListener(this));
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private int currentSlot;

    private final CoverManager coverManager;
    private final double speedMultiplier;

    public TieredImporterNetworkNode(Level level, BlockPos pos, CableTier tier) {
        super(level, pos, ContentType.IMPORTER, tier);
        this.coverManager    = new CoverManager(this);
        this.speedMultiplier = getSpeedMultiplier(1);
    }

    private UpgradeItem.Type[] getTierUpgrades() {
        return switch (getTier()) {
            case ELITE -> new UpgradeItem.Type[]{UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK};
            case ULTRA, MEGA -> new UpgradeItem.Type[]{UpgradeItem.Type.SPEED};
        };
    }

    @Override
    public int getEnergyUsage() {
        return (4 * (RS.SERVER_CONFIG.getImporter().getUsage() + upgrades.getEnergyUsage())) * getAdditionalEnergyCost();
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate() || !level.isLoaded(pos) || !level.isLoaded(pos.relative(getDirection()))) {
            return;
        }

        int speed = Math.max(0, upgrades.getSpeed((int) (BASE_SPEED / speedMultiplier), 2));
        if (speed != 0) {
            if (ticks % speed != 0) {
                return;
            }
        }

        if (type == IType.ITEMS) {
            BlockEntity facing = getFacingBlockEntity();
            IItemHandler handler = LevelUtils.getItemHandler(facing, getDirection().getOpposite());

            if (facing instanceof DiskDriveBlockEntity || handler == null) {
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
                    ItemStack result = handler.extractItem(currentSlot, getTieredStackInteractCount(upgrades), true);

                    if (!result.isEmpty() && network.insertItem(result, result.getCount(), Action.SIMULATE).isEmpty()) {
                        result = handler.extractItem(currentSlot, getTieredStackInteractCount(upgrades), false);

                        network.insertItemTracked(result, result.getCount());
                    } else {
                        currentSlot++;
                    }
                }
            }
        } else if (type == IType.FLUIDS) {
            IFluidHandler handler = LevelUtils.getFluidHandler(getFacingBlockEntity(), getDirection().getOpposite());

            if (handler != null) {
                FluidStack extractedSimulated = handler.drain(FluidType.BUCKET_VOLUME * getTieredStackInteractCount(upgrades), IFluidHandler.FluidAction.SIMULATE);

                if (!extractedSimulated.isEmpty()
                        && IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, extractedSimulated)
                        && network.insertFluid(extractedSimulated, extractedSimulated.getAmount(), Action.SIMULATE).isEmpty()) {
                    FluidStack extracted = handler.drain(extractedSimulated, IFluidHandler.FluidAction.EXECUTE);

                    if (!extracted.isEmpty()) {
                        network.insertFluidTracked(extracted, extracted.getAmount());
                    }
                }
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
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.coverManager.writeToNbt());

        StackUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(CoverManager.NBT_COVER_MANAGER)) {
            this.coverManager.readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));
        }

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
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
        return level.isClientSide ? TieredImporterBlockEntity.TYPE.getValue() : type;
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

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}
