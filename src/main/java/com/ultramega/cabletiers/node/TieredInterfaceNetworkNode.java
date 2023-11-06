package com.ultramega.cabletiers.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.SlottedCraftingRequest;
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.ItemExternalStorage;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ProxyItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class TieredInterfaceNetworkNode extends TieredNetworkNode<TieredInterfaceNetworkNode> implements IComparable {
    private static final String NBT_COMPARE = "Compare";

    private static final int BASE_SPEED = 9;

    private final BaseItemHandler importItems = new BaseItemHandler(getSlotAmount()).addListener(new NetworkNodeInventoryListener(this));

    private final BaseItemHandler exportFilterItems = new BaseItemHandler(getSlotAmount()) {
        @Override
        public int getSlotLimit(int slot) {
            return getTieredStackInteractCount();
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return getTieredStackInteractCount();
        }
    }.addListener(new NetworkNodeInventoryListener(this));
    private final BaseItemHandler exportItems = new BaseItemHandler(getSlotAmount()) {
        @Override
        public int getSlotLimit(int slot) {
            return getTieredStackInteractCount();
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return getTieredStackInteractCount();
        }
    }.addListener(new NetworkNodeInventoryListener(this));

    private final IItemHandler items = new ProxyItemHandler(importItems, exportItems);

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, getTierUpgrades())
            .addListener(new NetworkNodeInventoryListener(this));

    private int compare = IComparer.COMPARE_NBT;

    private int currentSlot = 0;

    private final double speedMultiplier;

    public TieredInterfaceNetworkNode(Level level, BlockPos pos, CableTier tier) {
        super(level, pos, ContentType.INTERFACE, tier);
        this.speedMultiplier = getSpeedMultiplier(5);
    }

    private UpgradeItem.Type[] getTierUpgrades() {
        return switch (getTier()) {
            case ELITE ->
                    new UpgradeItem.Type[]{UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK, UpgradeItem.Type.CRAFTING};
            case ULTRA, MEGA -> new UpgradeItem.Type[]{UpgradeItem.Type.SPEED, UpgradeItem.Type.CRAFTING};
        };
    }

    @Override
    public int getEnergyUsage() {
        return (4 * (RS.SERVER_CONFIG.getDiskManipulator().getUsage() + upgrades.getEnergyUsage())) * getAdditionalEnergyCost();
    }

    private int getSlotAmount() {
        return switch (getTier()) {
            case ELITE -> 9 * 2;
            case ULTRA -> 11 * 2;
            case MEGA -> 13 * 2;
        };
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate()) {
            return;
        }

        int speed = Math.max(0, upgrades.getSpeed((int) (BASE_SPEED / speedMultiplier), 2));
        if (speed != 0) {
            if (ticks % speed != 0) {
                return;
            }
        }

        if (currentSlot >= importItems.getSlots()) {
            currentSlot = 0;
        }

        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot.isEmpty()) {
            currentSlot++;
        } else {
            int size = Math.min(slot.getCount(), getTieredStackInteractCount(upgrades));

            ItemStack remainder = network.insertItemTracked(slot, size);

            importItems.extractItem(currentSlot, size - remainder.getCount(), false);
        }

        for (int i = 0; i < getSlotAmount(); ++i) {
            ItemStack wanted = exportFilterItems.getStackInSlot(i);
            ItemStack got = exportItems.getStackInSlot(i);

            if (wanted.isEmpty()) {
                if (!got.isEmpty()) {
                    exportItems.setStackInSlot(i, network.insertItemTracked(got, got.getCount()));
                }
            } else if (!got.isEmpty() && !API.instance().getComparer().isEqual(wanted, got, getCompare())) {
                exportItems.setStackInSlot(i, network.insertItemTracked(got, got.getCount()));
            } else {
                int delta = got.isEmpty() ? wanted.getCount() : (wanted.getCount() - got.getCount());

                if (delta > 0) {
                    final boolean actingAsStorage = isActingAsStorage();

                    ItemStack result = network.extractItem(wanted, delta, compare, Action.PERFORM, s -> {
                        // If we are not an interface acting as a storage, we can extract from anywhere.
                        if (!actingAsStorage) {
                            return true;
                        }

                        // If we are an interface acting as a storage, we don't want to extract from other interfaces to
                        // avoid stealing from each other.
                        return !(s instanceof ItemExternalStorage) || !((ItemExternalStorage) s).isConnectedToInterface();
                    });

                    if (!result.isEmpty()) {
                        if (exportItems.getStackInSlot(i).isEmpty()) {
                            exportItems.setStackInSlot(i, result);
                        } else {
                            exportItems.getStackInSlot(i).grow(result.getCount());
                        }
                    }

                    // Example: our delta is 5, we extracted 3 items.
                    // That means we still have to autocraft 2 items.
                    delta -= result.getCount();

                    if (delta > 0 && upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                        network.getCraftingManager().request(new SlottedCraftingRequest(this, i), wanted, delta);
                    }
                } else if (delta < 0) {
                    ItemStack remainder = network.insertItemTracked(got, Math.abs(delta));

                    exportItems.extractItem(i, Math.abs(delta) - remainder.getCount(), false);
                }
            }
        }
    }

    private boolean isActingAsStorage() {
        for (Direction facing : Direction.values()) {
            INetworkNode facingNode = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(pos.relative(facing));

            if (facingNode instanceof ExternalStorageNetworkNode && facingNode.isActive() && ((ExternalStorageNetworkNode) facingNode).getDirection() == facing.getOpposite() && ((ExternalStorageNetworkNode) facingNode).getType() == IType.ITEMS) {
                return true;
            }
        }

        return false;
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
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(importItems, 0, tag);
        StackUtils.readItems(exportItems, 2, tag);
        StackUtils.readItems(upgrades, 3, tag);
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(importItems, 0, tag);
        StackUtils.writeItems(exportItems, 2, tag);
        StackUtils.writeItems(upgrades, 3, tag);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(exportFilterItems, 1, tag);
        for (int i = 0; i < exportFilterItems.getSlots(); i++) {
            if (!exportFilterItems.getStackInSlot(i).isEmpty()) {
                tag.putInt("SlotItemAmount" + i, exportFilterItems.getStackInSlot(i).getCount());
            }
        }

        tag.putInt(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(exportFilterItems, 1, tag);
        for (int i = 0; i < exportFilterItems.getSlots(); i++) {
            exportFilterItems.getStackInSlot(i).setCount(tag.getInt("SlotItemAmount" + i));
        }

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }
    }

    public IItemHandler getImportItems() {
        return importItems;
    }

    public IItemHandler getExportFilterItems() {
        return exportFilterItems;
    }

    public IItemHandler getExportItems() {
        return exportItems;
    }

    public IItemHandler getItems() {
        return items;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(importItems, exportItems, upgrades);
    }
}
