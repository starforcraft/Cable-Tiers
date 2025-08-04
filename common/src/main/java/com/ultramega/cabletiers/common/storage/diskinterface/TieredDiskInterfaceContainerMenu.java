package com.ultramega.cabletiers.common.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.storage.StorageContainerItem;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.storage.DiskInventory;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ValidatedSlot;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;

import java.util.function.Predicate;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredDiskInterfaceContainerMenu extends AbstractTieredFilterContainerMenu<AbstractTieredDiskInterfaceBlockEntity> {
    private static final MutableComponent FILTER_HELP = createTranslation("gui", "disk_interface.filter_help");

    private static final int DISK_SLOT_X1 = 44;
    private static final int DISK_SLOT_X2 = 116;
    private static final int DISK_SLOT_Y = 57;

    private final Predicate<Player> stillValid;

    TieredDiskInterfaceContainerMenu(final int syncId,
                                     final Player player,
                                     final AbstractTieredDiskInterfaceBlockEntity blockEntity,
                                     final DiskInventory diskInventory,
                                     final ResourceContainer filterContainer,
                                     final UpgradeContainer upgradeContainer,
                                     final CableTiers tier) {
        super(Menus.INSTANCE.getTieredDiskInterfaces(tier),
            syncId,
            player,
            filterContainer,
            tier != CableTiers.CREATIVE ? upgradeContainer : null,
            blockEntity,
            129 + getYIncrease(tier),
            FILTER_HELP,
            tier);
        addSlots(player, diskInventory);
        this.playerInventoryY = 129;
        this.stillValid = p -> Container.stillValidBlockEntity(blockEntity, p);
    }

    public TieredDiskInterfaceContainerMenu(final int syncId,
                                            final Inventory playerInventory,
                                            final ResourceContainerData data,
                                            final CableTiers tier) {
        super(Menus.INSTANCE.getTieredDestructors(tier),
            syncId,
            playerInventory.player,
            data,
            tier != CableTiers.CREATIVE ? AbstractTieredDiskInterfaceBlockEntity.getUpgradeDestination(tier) : null,
            129 + getYIncrease(tier),
            FILTER_HELP,
            tier);
        addSlots(
            playerInventory.player,
            new FilteredContainer(
                AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS,
                StorageContainerItem.stackValidator()
            )
        );
        this.stillValid = p -> true;
    }

    private void addSlots(final Player player,
                          final FilteredContainer diskInventory) {
        for (int i = 0; i < diskInventory.getContainerSize(); ++i) {
            addSlot(createDiskSlot(diskInventory, i));
        }
        transferManager.addBiTransfer(player.getInventory(), diskInventory);
    }

    @Override
    protected void registerClientProperties() {
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        registerProperty(new ClientProperty<>(PropertyTypes.FUZZY_MODE, false));
        registerProperty(new ClientProperty<>(PropertyTypes.FILTER_MODE, FilterMode.BLOCK));
        registerProperty(new ClientProperty<>(
            DiskInterfacePropertyTypes.TRANSFER_MODE,
            StorageTransferMode.INSERT_INTO_NETWORK
        ));
    }

    @Override
    protected void registerServerProperties(final AbstractTieredDiskInterfaceBlockEntity blockEntity) {
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            blockEntity::getRedstoneMode,
            blockEntity::setRedstoneMode
        ));
        registerProperty(new ServerProperty<>(
            PropertyTypes.FUZZY_MODE,
            blockEntity::isFuzzyMode,
            blockEntity::setFuzzyMode
        ));
        registerProperty(new ServerProperty<>(
            PropertyTypes.FILTER_MODE,
            blockEntity::getFilterMode,
            blockEntity::setFilterMode
        ));
        registerProperty(new ServerProperty<>(
            DiskInterfacePropertyTypes.TRANSFER_MODE,
            blockEntity::getTransferMode,
            blockEntity::setTransferMode
        ));
    }

    private Slot createDiskSlot(final FilteredContainer diskInventory, final int i) {
        final int x = i < 3 ? DISK_SLOT_X1 : DISK_SLOT_X2;
        final int y = DISK_SLOT_Y + ((i % 3) * 18) + getYIncrease(tier);
        return ValidatedSlot.forStorageContainer(diskInventory, i, x, y);
    }

    public static int getYIncrease(final CableTiers tier) {
        return ((tier.getFilterSlotsCount() / 9 - 1) * 18);
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid.test(player);
    }
}
