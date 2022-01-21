package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.config.CableConfig;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ProxyItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TieredDiskManipulatorNetworkNode extends TieredNetworkNode<TieredDiskManipulatorNetworkNode> implements IComparable, IWhitelistBlacklist, IType, IStorageDiskContainerContext {

    public static final int IO_MODE_INSERT = 0;
    public static final int IO_MODE_EXTRACT = 1;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_IO_MODE = "IOMode";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private int ioMode = IO_MODE_INSERT;

    private final IStorageDisk<ItemStack>[] itemDisks = new IStorageDisk[6 * checkTierMultiplier()];
    private final IStorageDisk<FluidStack>[] fluidDisks = new IStorageDisk[6 * checkTierMultiplier()];

    private final UpgradeItemHandler upgrades;

    private final BaseItemHandler inputDisks;
    private final BaseItemHandler outputDisks;

    private final ProxyItemHandler disks;

    private final BaseItemHandler itemFilters = new BaseItemHandler(9 * checkTierMultiplier()).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9 * checkTierMultiplier()).addListener(new NetworkNodeFluidInventoryListener(this));

    public TieredDiskManipulatorNetworkNode(Level level, BlockPos pos, CableTier tier) {
        super(level, pos, ContentType.DISK_MANIPULATOR, tier);
        this.upgrades = (UpgradeItemHandler) new UpgradeItemHandler(getTier() == CableTier.CREATIVE ? 0 : 4, CheckTierUpgrade()) {
            @Override
            public int getStackInteractCount() {
                int count = super.getStackInteractCount();

                if (type == IType.FLUIDS) {
                    count *= FluidAttributes.BUCKET_VOLUME;
                }

                return count;
            }
        }.addListener(new NetworkNodeInventoryListener(this));
        this.outputDisks = new BaseItemHandler(3 * checkTierMultiplier())
                .addValidator(new StorageDiskItemValidator())
                .addListener(new NetworkNodeInventoryListener(this))
                .addListener(((handler, slot, reading) -> {
                    if (!level.isClientSide) {
                        StackUtils.createStorages(
                                (ServerLevel) level,
                                handler.getStackInSlot(slot),
                                3 * checkTierMultiplier() + slot,
                                itemDisks,
                                fluidDisks,
                                s -> new TieredStorageDiskItemManipulatorWrapper(TieredDiskManipulatorNetworkNode.this, s),
                                s -> new TieredStorageDiskFluidManipulatorWrapper(TieredDiskManipulatorNetworkNode.this, s)
                        );

                        if (!reading) {
                            LevelUtils.updateBlock(level, pos);
                        }
                    }
                }));
        this.inputDisks = new BaseItemHandler(3 * checkTierMultiplier())
                .addValidator(new StorageDiskItemValidator())
                .addListener(new NetworkNodeInventoryListener(this))
                .addListener((handler, slot, reading) -> {
                    if (!level.isClientSide) {
                        StackUtils.createStorages(
                                (ServerLevel) level,
                                handler.getStackInSlot(slot),
                                slot,
                                itemDisks,
                                fluidDisks,
                                s -> new TieredStorageDiskItemManipulatorWrapper(TieredDiskManipulatorNetworkNode.this, s),
                                s -> new TieredStorageDiskFluidManipulatorWrapper(TieredDiskManipulatorNetworkNode.this, s)
                        );

                        if (!reading) {
                            LevelUtils.updateBlock(level, pos);
                        }
                    }
                });
        this.disks = new ProxyItemHandler(inputDisks, outputDisks);
    }

    private UpgradeItem.Type[] CheckTierUpgrade()
    {
        if(getTier() == CableTier.ELITE)
        {
            return new UpgradeItem.Type[] { UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK };
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return new UpgradeItem.Type[] { UpgradeItem.Type.SPEED };
        }
        else if(getTier() == CableTier.CREATIVE)
        {
            return new UpgradeItem.Type[] { };
        }
        return null;
    }

    @Override
    public int getEnergyUsage() {
        if(getTier() == CableTier.ELITE)
        {
            return (4 * (RS.SERVER_CONFIG.getDiskManipulator().getUsage() + upgrades.getEnergyUsage())) * CableConfig.ELITE_ENERGY_COST.get();
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return (4 * (RS.SERVER_CONFIG.getDiskManipulator().getUsage() + upgrades.getEnergyUsage())) * CableConfig.ULTRA_ENERGY_COST.get();
        }
        else if(getTier() == CableTier.CREATIVE)
        {
            return (4 * (RS.SERVER_CONFIG.getDiskManipulator().getUsage() + upgrades.getEnergyUsage())) * CableConfig.CREATIVE_ENERGY_COST.get();
        }
        return 0;
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate())
        {
            return;
        }

        if(getTier() != CableTier.CREATIVE)
        {
            if (ticks % upgrades.getSpeed() != 0) {
                return;
            }
        }

        int slot = 0;
        if (type == IType.ITEMS) {
            while (slot < 3 * checkTierMultiplier() && (itemDisks[slot] == null || isItemDiskDone(itemDisks[slot], slot))) {
                slot++;
            }

            if (slot == 3 * checkTierMultiplier()) {
                return;
            }

            IStorageDisk<ItemStack> storage = itemDisks[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertItemIntoNetwork(storage);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractItemFromNetwork(storage, slot);
            }
        } else if (type == IType.FLUIDS) {
            while (slot < 3 * checkTierMultiplier() && (fluidDisks[slot] == null || isFluidDiskDone(fluidDisks[slot], slot))) {
                slot++;
            }

            if (slot == 3 * checkTierMultiplier()) {
                return;
            }

            IStorageDisk<FluidStack> storage = fluidDisks[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertFluidIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFluidFromNetwork(storage, slot);
            }
        }
    }

    private void insertItemIntoNetwork(IStorageDisk<ItemStack> storage) {
        List<ItemStack> stacks = new ArrayList<>(storage.getStacks());

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get(i);

            ItemStack extracted = storage.extract(stack, getStackInteractCount(), compare, Action.PERFORM);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), Action.PERFORM);
            if (remainder.isEmpty()) {
                break;
            }

            // We need to check if the stack was inserted
            storage.insert(((extracted == remainder) ? remainder.copy() : remainder), remainder.getCount(), Action.PERFORM);
        }
    }

    // Iterate through disk stacks, if none can be inserted, return that it is done processing and can be output.
    private boolean isItemDiskDone(IStorageDisk<ItemStack> storage, int slot) {
        if (ioMode == IO_MODE_INSERT && storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        // In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT) {
            if (storage.getStored() == storage.getCapacity()) {
                moveDriveToOutput(slot);
                return true;
            } else {
                return false;
            }
        }

        List<ItemStack> stacks = new ArrayList<>(storage.getStacks());

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get(i);

            ItemStack extracted = storage.extract(stack, getStackInteractCount(), compare, Action.SIMULATE);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), Action.SIMULATE);
            if (remainder.isEmpty()) { // An item could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }

        return true;
    }

    private void extractItemFromNetwork(IStorageDisk<ItemStack> storage, int slot) {
        ItemStack extracted = ItemStack.EMPTY;
        int i = 0;

        if (itemFilters.isEmpty()) {
            ItemStack toExtract = null;
            List<ItemStack> networkItems = network.getItemStorageCache().getList().getStacks().stream().map(StackListEntry::getStack).collect(Collectors.toList());

            int j = 0;

            while ((toExtract == null || toExtract.isEmpty()) && j < networkItems.size()) {
                toExtract = networkItems.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractItem(toExtract, getStackInteractCount(), compare, Action.PERFORM);
            }
        } else {
            while (itemFilters.getSlots() > i && extracted.isEmpty()) {
                ItemStack filterStack = ItemStack.EMPTY;

                while (itemFilters.getSlots() > i && filterStack.isEmpty()) {
                    filterStack = itemFilters.getStackInSlot(i++);
                }

                if (!filterStack.isEmpty()) {
                    extracted = network.extractItem(filterStack, getStackInteractCount(), compare, Action.PERFORM);
                }
            }
        }

        if (extracted.isEmpty()) {
            moveDriveToOutput(slot);
            return;
        }

        ItemStack remainder = storage.insert(extracted, extracted.getCount(), Action.PERFORM);

        network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
    }

    private void insertFluidIntoNetwork(IStorageDisk<FluidStack> storage, int slot) {
        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());

        FluidStack extracted = FluidStack.EMPTY;
        int i = 0;

        while (extracted.isEmpty() && stacks.size() > i) {
            FluidStack stack = stacks.get(i++);

            extracted = storage.extract(stack, getStackInteractCount(), compare, Action.PERFORM);
        }

        if (extracted.isEmpty()) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = network.insertFluid(extracted, extracted.getAmount(), Action.PERFORM);

        storage.insert(remainder, remainder.getAmount(), Action.PERFORM);
    }

    private boolean isFluidDiskDone(IStorageDisk<FluidStack> storage, int slot) {
        if (ioMode == IO_MODE_INSERT && storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        //In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT) {
            if (storage.getStored() == storage.getCapacity()) {
                moveDriveToOutput(slot);
                return true;
            } else {
                return false;
            }
        }

        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());

        for (int i = 0; i < stacks.size(); ++i) {
            FluidStack stack = stacks.get(i);

            FluidStack extracted = storage.extract(stack, getStackInteractCount(), compare, Action.SIMULATE);
            if (extracted.isEmpty()) {
                continue;
            }

            FluidStack remainder = network.insertFluid(extracted, extracted.getAmount(), Action.SIMULATE);
            if (remainder.isEmpty()) { // A fluid could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }

        return true;
    }

    private void extractFluidFromNetwork(IStorageDisk<FluidStack> storage, int slot) {
        FluidStack extracted = FluidStack.EMPTY;
        int i = 0;

        if (fluidFilters.isEmpty()) {
            FluidStack toExtract = null;
            List<FluidStack> networkFluids = network.getFluidStorageCache().getList().getStacks().stream().map(StackListEntry::getStack).collect(Collectors.toList());

            int j = 0;

            while ((toExtract == null || toExtract.getAmount() == 0) && j < networkFluids.size()) {
                toExtract = networkFluids.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractFluid(toExtract, getStackInteractCount(), compare, Action.PERFORM);
            }
        } else {
            while (fluidFilters.getSlots() > i && extracted.isEmpty()) {
                FluidStack filterStack = FluidStack.EMPTY;

                while (fluidFilters.getSlots() > i && filterStack.isEmpty()) {
                    filterStack = fluidFilters.getFluid(i++);
                }

                if (!filterStack.isEmpty()) {
                    extracted = network.extractFluid(filterStack, getStackInteractCount(), compare, Action.PERFORM);
                }
            }
        }

        if (extracted.isEmpty()) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = storage.insert(extracted, extracted.getAmount(), Action.PERFORM);

        network.insertFluid(remainder, remainder.getAmount(), Action.PERFORM);
    }

    private void moveDriveToOutput(int slot) {
        ItemStack disk = inputDisks.getStackInSlot(slot);
        if (!disk.isEmpty()) {
            int i = 0;
            while (i < 3 * checkTierMultiplier() && !outputDisks.getStackInSlot(i).isEmpty()) {
                i++;
            }

            if (i == 3 * checkTierMultiplier()) {
                return;
            }

            inputDisks.extractItem(slot, 1, false);
            outputDisks.insertItem(i, disk, false);
        }
    }

    public DiskState[] getDiskState() {
        DiskState[] diskStates = new DiskState[6 * checkTierMultiplier()];

        for (int i = 0; i < 6 * checkTierMultiplier(); ++i) {
            DiskState state = DiskState.NONE;

            if (itemDisks[i] != null || fluidDisks[i] != null) {
                if (!canUpdate()) {
                    state = DiskState.DISCONNECTED;
                } else {
                    state = DiskState.get(
                            itemDisks[i] != null ? itemDisks[i].getStored() : fluidDisks[i].getStored(),
                            itemDisks[i] != null ? itemDisks[i].getCapacity() : fluidDisks[i].getCapacity()
                    );
                }
            }

            diskStates[i] = state;
        }

        return diskStates;
    }

    private int getStackInteractCount()
    {
        if(getTier() == CableTier.CREATIVE)
        {
            return Integer.MAX_VALUE;
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return 64 * CableConfig.ULTRA_DISK_MANIPULATOR_SPEED.get();
        }
        else if(getTier() == CableTier.ELITE)
        {
            return upgrades.getStackInteractCount() * CableConfig.ELITE_DISK_MANIPULATOR_SPEED.get();
        }
        return 0;
    }

    private int checkTierMultiplier()
    {
        if(getTier() == CableTier.ELITE)
        {
            return 2;
        }
        else if(getTier() == CableTier.ULTRA)
        {
            return 3;
        }
        else if(getTier() == CableTier.CREATIVE)
        {
            return 4;
        }
        return 0;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;
    }

    @Override
    public int getType() {
        return level.isClientSide ? DiskManipulatorBlockEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
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
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getWhitelistBlacklistMode() {
        return this.mode;
    }

    public int getIoMode() {
        return ioMode;
    }

    public void setIoMode(int ioMode) {
        this.ioMode = ioMode;
    }

    public IItemHandler getInputDisks() {
        return inputDisks;
    }

    public IItemHandler getOutputDisks() {
        return outputDisks;
    }

    public ProxyItemHandler getDisks() {
        return disks;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 3, tag);
        StackUtils.readItems(inputDisks, 4, tag);
        StackUtils.readItems(outputDisks, 5, tag);
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 3, tag);
        StackUtils.writeItems(inputDisks, 4, tag);
        StackUtils.writeItems(outputDisks, 5, tag);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        tag.putInt(NBT_IO_MODE, ioMode);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 1, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        if (tag.contains(NBT_IO_MODE)) {
            ioMode = tag.getInt(NBT_IO_MODE);
        }
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(inputDisks, outputDisks, upgrades);
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }

    public class TieredStorageDiskFluidManipulatorWrapper implements IStorageDisk<FluidStack> {
        private final TieredDiskManipulatorNetworkNode diskManipulator;
        private final IStorageDisk<FluidStack> parent;
        private DiskState lastState;

        public TieredStorageDiskFluidManipulatorWrapper(TieredDiskManipulatorNetworkNode diskManipulator, IStorageDisk<FluidStack> parent) {
            this.diskManipulator = diskManipulator;
            this.parent = parent;
            this.setSettings(
                    () -> {
                        DiskState currentState = DiskState.get(getStored(), getCapacity());

                        if (lastState != currentState) {
                            lastState = currentState;

                            LevelUtils.updateBlock(diskManipulator.getLevel(), diskManipulator.getPos());
                        }
                    },
                    diskManipulator
            );
            this.lastState = DiskState.get(getStored(), getCapacity());
        }

        @Override
        public int getCapacity() {
            return parent.getCapacity();
        }

        @Nullable
        @Override
        public UUID getOwner() {
            return parent.getOwner();
        }

        @Override
        public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
            parent.setSettings(listener, context);
        }

        @Override
        public CompoundTag writeToNbt() {
            return parent.writeToNbt();
        }

        @Override
        public ResourceLocation getFactoryId() {
            return parent.getFactoryId();
        }

        @Override
        public Collection<FluidStack> getStacks() {
            return parent.getStacks();
        }

        @Override
        @Nonnull
        public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
            if (stack.isEmpty()) {
                return stack;
            }

            if (!IWhitelistBlacklist.acceptsFluid(diskManipulator.getFluidFilters(), diskManipulator.getWhitelistBlacklistMode(), diskManipulator.getCompare(), stack)) {
                return StackUtils.copy(stack, size);
            }

            return parent.insert(stack, size, action);
        }

        @Override
        @Nonnull
        public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
            if (!IWhitelistBlacklist.acceptsFluid(diskManipulator.getFluidFilters(), diskManipulator.getWhitelistBlacklistMode(), diskManipulator.getCompare(), stack)) {
                return FluidStack.EMPTY;
            }

            return parent.extract(stack, size, flags, action);
        }

        @Override
        public int getStored() {
            return parent.getStored();
        }

        @Override
        public int getPriority() {
            return parent.getPriority();
        }

        @Override
        public AccessType getAccessType() {
            return parent.getAccessType();
        }

        @Override
        public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
            return parent.getCacheDelta(storedPreInsertion, size, remainder);
        }
    }

    public class TieredStorageDiskItemManipulatorWrapper implements IStorageDisk<ItemStack> {
        private final TieredDiskManipulatorNetworkNode diskManipulator;
        private final IStorageDisk<ItemStack> parent;
        private DiskState lastState;

        public TieredStorageDiskItemManipulatorWrapper(TieredDiskManipulatorNetworkNode diskManipulator, IStorageDisk<ItemStack> parent) {
            this.diskManipulator = diskManipulator;
            this.parent = parent;
            this.setSettings(
                    () -> {
                        DiskState currentState = DiskState.get(getStored(), getCapacity());

                        if (lastState != currentState) {
                            lastState = currentState;

                            LevelUtils.updateBlock(diskManipulator.getLevel(), diskManipulator.getPos());
                        }
                    },
                    diskManipulator
            );
            this.lastState = DiskState.get(getStored(), getCapacity());
        }

        @Override
        public int getCapacity() {
            return parent.getCapacity();
        }

        @Nullable
        @Override
        public UUID getOwner() {
            return parent.getOwner();
        }

        @Override
        public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
            parent.setSettings(listener, context);
        }

        @Override
        public CompoundTag writeToNbt() {
            return parent.writeToNbt();
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return parent.getStacks();
        }

        @Override
        @Nonnull
        public ItemStack insert(@Nonnull ItemStack stack, int size, Action action) {
            if (stack.isEmpty()) {
                return stack;
            }

            if (!IWhitelistBlacklist.acceptsItem(diskManipulator.getItemFilters(), diskManipulator.getWhitelistBlacklistMode(), diskManipulator.getCompare(), stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return parent.insert(stack, size, action);
        }

        @Override
        @Nonnull
        public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, Action action) {
            if (!IWhitelistBlacklist.acceptsItem(diskManipulator.getItemFilters(), diskManipulator.getWhitelistBlacklistMode(), diskManipulator.getCompare(), stack)) {
                return ItemStack.EMPTY;
            }

            return parent.extract(stack, size, flags, action);
        }

        @Override
        public int getStored() {
            return parent.getStored();
        }

        @Override
        public int getPriority() {
            return parent.getPriority();
        }

        @Override
        public AccessType getAccessType() {
            return parent.getAccessType();
        }

        @Override
        public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
            return parent.getCacheDelta(storedPreInsertion, size, remainder);
        }

        @Override
        public ResourceLocation getFactoryId() {
            return parent.getFactoryId();
        }
    }
}
