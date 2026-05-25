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

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
            this.mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.DISK_INTERFACE) + upgradeEnergyUsage);
        }, this::setChanged) {
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
        this.ticker = this.upgradeContainer.getTicker();
        this.mainNetworkNode.setListener(this);
        this.mainNetworkNode.setTransferQuotaProvider(storage -> {
            if (storage instanceof SerializableStorage serializableStorage) {
                return serializableStorage.getType().getDiskInterfaceTransferQuota(this.upgradeContainer.has(Items.INSTANCE.getStackUpgrade()));
            }
            return 1;
        });
        this.mainNetworkNode.setStackUpgradeProvider(() -> this.upgradeContainer.has(Items.INSTANCE.getStackUpgrade()));
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier.hasIntegratedStackUpgrade(CableType.DISK_INTERFACE)
            ? TieredUpgradeDestinations.DISK_INTERFACE_NO_STACK
            : UpgradeDestinations.DISK_INTERFACE;
    }

    @Override
    protected void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        this.mainNetworkNode.setFilters(filters, tagFilters);
    }

    @Override
    protected void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        this.mainNetworkNode.setNormalizer(normalizer);
    }

    @Override
    public void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_UPGRADES, ItemContainerContents.CODEC, ItemContainerContents.fromItems(this.upgradeContainer.getUpgrades()));
    }

    @Override
    public void loadAdditional(final ValueInput input) {
        input.read(TAG_UPGRADES, ItemContainerContents.CODEC).ifPresent(this.upgradeContainer::load);
        super.loadAdditional(input);
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        output.putInt(TAG_TRANSFER_MODE, TransferModeSettings.getTransferMode(this.mainNetworkNode.getMode()));
        output.putInt(TAG_FILTER_MODE, FilterModeSettings.getFilterMode(this.mainNetworkNode.getFilterMode()));
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        input.getInt(TAG_TRANSFER_MODE).map(TransferModeSettings::getTransferMode).ifPresent(this.mainNetworkNode::setMode);
        input.getInt(TAG_FILTER_MODE).map(FilterModeSettings::getFilterMode).ifPresent(this.mainNetworkNode::setFilterMode);
    }

    @Override
    public List<ItemStack> getUpgrades() {
        return this.upgradeContainer.getUpgrades();
    }

    @Override
    public boolean addUpgrade(final ItemStack upgradeStack) {
        return this.upgradeContainer.addUpgrade(upgradeStack);
    }

    @Override
    public Component getName() {
        return this.overrideName(this.tier.getContentName(CableType.DISK_INTERFACE));
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inv, final Player player) {
        this.setInContainerMenu(true);

        return new TieredDiskInterfaceContainerMenu(
            syncId,
            player,
            this,
            this.diskInventory,
            this.filter.getFilterContainer(),
            this.upgradeContainer,
            this.tier
        );
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this.upgradeContainer.getItems());
        }
    }

    boolean isFuzzyMode() {
        return this.filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        this.filter.setFuzzyMode(fuzzyMode);
        this.setChanged();
    }

    FilterMode getFilterMode() {
        return this.mainNetworkNode.getFilterMode();
    }

    void setFilterMode(final FilterMode mode) {
        this.mainNetworkNode.setFilterMode(mode);
        this.setChanged();
    }

    public StorageTransferMode getTransferMode() {
        return this.mainNetworkNode.getMode();
    }

    public void setTransferMode(final StorageTransferMode mode) {
        this.mainNetworkNode.setMode(mode);
        this.setChanged();
    }

    @Override
    public void onTransferSuccess(final int index) {
        final ItemStack diskStack = this.diskInventory.getItem(index);
        if (diskStack.isEmpty()) {
            return;
        }
        for (int newIndex = AMOUNT_OF_DISKS / 2; newIndex < AMOUNT_OF_DISKS; ++newIndex) {
            if (!this.diskInventory.getItem(newIndex).isEmpty()) {
                continue;
            }
            this.diskInventory.setItem(index, ItemStack.EMPTY);
            this.diskInventory.setItem(newIndex, diskStack);
            return;
        }
    }
}
