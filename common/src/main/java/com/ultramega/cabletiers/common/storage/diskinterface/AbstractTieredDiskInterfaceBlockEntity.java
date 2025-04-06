package com.ultramega.cabletiers.common.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.TieredUpgradeDestinations;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.storage.AbstractTieredDiskContainerBlockEntity;
import com.ultramega.cabletiers.common.storage.AdvancedStorageTransferNetworkNode;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferListener;
import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeItem;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.FilterModeSettings;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTieredDiskInterfaceBlockEntity extends AbstractTieredDiskContainerBlockEntity<AdvancedStorageTransferNetworkNode>
    implements StorageTransferListener {
    public static final int AMOUNT_OF_DISKS = 6;

    private static final String TAG_UPGRADES = "upgr";
    private static final String TAG_FILTER_MODE = "fim";
    private static final String TAG_TRANSFER_MODE = "tm";

    private final UpgradeContainer upgradeContainer;

    protected AbstractTieredDiskInterfaceBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getTieredDiskInterfaces(tier), pos, state, new AdvancedStorageTransferNetworkNode(
            tier.getEnergyUsage(CableType.DISK_INTERFACE),
            com.refinedmods.refinedstorage.common.Platform.INSTANCE.getConfig().getDiskInterface().getEnergyUsagePerDisk(),
            AMOUNT_OF_DISKS
        ), tier, CableType.DISK_INTERFACE);
        this.upgradeContainer = new UpgradeContainer(getUpgradeDestination(tier), (c, upgradeEnergyUsage) -> {
            mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.DISK_INTERFACE) + upgradeEnergyUsage);
            setChanged();
        }) {
            @Override
            public boolean has(final UpgradeItem upgradeItem) {
                if (tier.hasIntegratedStackUpgrade(CableType.DISK_INTERFACE) && upgradeItem == Items.INSTANCE.getStackUpgrade()) {
                    return true;
                }
                return super.has(upgradeItem);
            }

            @Override
            public int getAmount(final UpgradeItem upgradeItem) {
                if (tier == CableTiers.CREATIVE && upgradeItem == Items.INSTANCE.getSpeedUpgrade()) {
                    return 4;
                }
                return super.getAmount(upgradeItem);
            }
        };
        this.ticker = upgradeContainer.getTicker();
        this.mainNetworkNode.setListener(this);
        this.mainNetworkNode.setTransferQuotaProvider(storage -> {
            if (storage instanceof SerializableStorage serializableStorage) {
                return serializableStorage.getType().getDiskInterfaceTransferQuota(upgradeContainer.has(Items.INSTANCE.getStackUpgrade()))
                    * tier.getSpeed(CableType.DISK_INTERFACE);
            }
            return tier.getSpeed(CableType.DISK_INTERFACE);
        });
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier.hasIntegratedStackUpgrade(CableType.DISK_INTERFACE)
            ? TieredUpgradeDestinations.DISK_INTERFACE_NO_STACK
            : UpgradeDestinations.DISK_INTERFACE;
    }

    @Override
    protected void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        mainNetworkNode.setFilters(filters, tagFilters);
    }

    @Override
    protected void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        mainNetworkNode.setNormalizer(normalizer);
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
        tag.putInt(TAG_FILTER_MODE, FilterModeSettings.getFilterMode(mainNetworkNode.getFilterMode()));
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        if (tag.contains(TAG_TRANSFER_MODE)) {
            mainNetworkNode.setMode(TransferModeSettings.getTransferMode(tag.getInt(TAG_TRANSFER_MODE)));
        }
        if (tag.contains(TAG_FILTER_MODE)) {
            mainNetworkNode.setFilterMode(FilterModeSettings.getFilterMode(tag.getInt(TAG_FILTER_MODE)));
        }
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        tag.putInt(TAG_TRANSFER_MODE, TransferModeSettings.getTransferMode(mainNetworkNode.getMode()));
    }

    @Override
    public List<ItemStack> getUpgrades() {
        return upgradeContainer.getUpgrades();
    }

    @Override
    public boolean addUpgrade(final ItemStack upgradeStack) {
        return upgradeContainer.addUpgrade(upgradeStack);
    }

    @Override
    public Component getName() {
        return overrideName(tier.getContentName(CableType.DISK_INTERFACE));
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inv, final Player player) {
        this.setInContainerMenu(true);

        return new TieredDiskInterfaceContainerMenu(
            syncId,
            player,
            this,
            diskInventory,
            filter.getFilterContainer(),
            upgradeContainer,
            tier
        );
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        final NonNullList<ItemStack> drops = super.getDrops();
        for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
            drops.add(upgradeContainer.getItem(i));
        }
        return drops;
    }

    boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
        setChanged();
    }

    FilterMode getFilterMode() {
        return mainNetworkNode.getFilterMode();
    }

    void setFilterMode(final FilterMode mode) {
        mainNetworkNode.setFilterMode(mode);
        setChanged();
    }

    public StorageTransferMode getTransferMode() {
        return mainNetworkNode.getMode();
    }

    public void setTransferMode(final StorageTransferMode mode) {
        mainNetworkNode.setMode(mode);
        setChanged();
    }

    @Override
    public void onTransferSuccess(final int index) {
        final ItemStack diskStack = diskInventory.getItem(index);
        if (diskStack.isEmpty()) {
            return;
        }
        for (int newIndex = AMOUNT_OF_DISKS / 2; newIndex < AMOUNT_OF_DISKS; ++newIndex) {
            if (!diskInventory.getItem(newIndex).isEmpty()) {
                continue;
            }
            diskInventory.setItem(index, ItemStack.EMPTY);
            diskInventory.setItem(newIndex, diskStack);
            return;
        }
    }
}
