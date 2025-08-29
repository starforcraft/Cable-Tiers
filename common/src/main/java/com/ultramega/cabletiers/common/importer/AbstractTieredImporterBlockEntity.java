package com.ultramega.cabletiers.common.importer;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.TieredUpgradeDestinations;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.support.AbstractTieredCableLikeBlockEntity;

import com.refinedmods.refinedstorage.api.network.impl.node.importer.CompositeImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.importer.ImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeItem;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.FilterModeSettings;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTieredImporterBlockEntity extends AbstractTieredCableLikeBlockEntity<TieredImporterNetworkNode>
    implements NetworkNodeExtendedMenuProvider<ResourceContainerData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTieredImporterBlockEntity.class);

    protected AbstractTieredImporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getTieredImporters(tier),
            pos,
            state,
            new TieredImporterNetworkNode(tier.getEnergyUsage(CableType.IMPORTER)),
            tier,
            CableType.IMPORTER);
        this.filter = TagFilterWithFuzzyMode.createAndListenForUniqueFilters(
            AdvancedResourceContainerImpl.createForFilter(tier),
            this::setChanged,
            this::setFilters
        );
        this.mainNetworkNode.setNormalizer(filter.createNormalizer());
        this.upgradeContainer = new UpgradeContainer(getUpgradeDestination(tier), (c, upgradeEnergyUsage) -> {
            mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.IMPORTER) + upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        }) {
            @Override
            public boolean has(final UpgradeItem upgradeItem) {
                if (tier.hasIntegratedStackUpgrade(CableType.IMPORTER) && upgradeItem == Items.INSTANCE.getStackUpgrade()) {
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
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier == CableTiers.CREATIVE
            ? TieredUpgradeDestinations.IMPORTER_NO_STACK_SPEED
            : tier.hasIntegratedStackUpgrade(CableType.IMPORTER)
            ? TieredUpgradeDestinations.IMPORTER_NO_STACK
            : UpgradeDestinations.IMPORTER;
    }

    void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        mainNetworkNode.setFilters(filters, tagFilters);
    }

    boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
    }

    FilterMode getFilterMode() {
        return mainNetworkNode.getFilterMode();
    }

    void setFilterMode(final FilterMode mode) {
        mainNetworkNode.setFilterMode(mode);
        setChanged();
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        tag.putInt(TAG_FILTER_MODE, FilterModeSettings.getFilterMode(mainNetworkNode.getFilterMode()));
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        if (tag.contains(TAG_FILTER_MODE)) {
            mainNetworkNode.setFilterMode(FilterModeSettings.getFilterMode(tag.getInt(TAG_FILTER_MODE)));
        }
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        final ImporterTransferStrategy strategy = createStrategy(level, direction, worldPosition, upgradeContainer);
        LOGGER.debug("Initialized importer at {} with strategy {}", worldPosition, strategy);
        mainNetworkNode.setTransferStrategy(strategy);
    }

    public static ImporterTransferStrategy createStrategy(final ServerLevel serverLevel,
                                                          final Direction direction,
                                                          final BlockPos worldPosition,
                                                          final UpgradeContainer upgradeContainer) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = worldPosition.relative(direction);
        final List<ImporterTransferStrategyFactory> factories =
            RefinedStorageApi.INSTANCE.getImporterTransferStrategyRegistry().getAll();
        return new CompositeImporterTransferStrategy(factories
            .stream()
            .map(factory -> factory.create(serverLevel, sourcePosition, incomingDirection, upgradeContainer))
            .toList());
    }

    @Override
    public ResourceContainerData getMenuData() {
        return ResourceContainerData.of(filter.getFilterContainer());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ResourceContainerData> getMenuCodec() {
        return ResourceContainerData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return overrideName(tier.getContentName(CableType.IMPORTER));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        this.setInContainerMenu(true);

        return new TieredImporterContainerMenu(syncId, player, this, filter.getFilterContainer(), upgradeContainer, tier);
    }
}
