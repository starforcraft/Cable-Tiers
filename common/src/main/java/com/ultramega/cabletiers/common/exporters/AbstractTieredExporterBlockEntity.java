package com.ultramega.cabletiers.common.exporters;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.TieredUpgradeDestinations;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.support.AbstractTieredCableLikeBlockEntity;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeItem;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.exporter.ExporterData;
import com.refinedmods.refinedstorage.common.support.SchedulingModeContainer;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTieredExporterBlockEntity extends AbstractTieredCableLikeBlockEntity<TieredExporterNetworkNode>
    implements NetworkNodeExtendedMenuProvider<ExporterData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTieredExporterBlockEntity.class);

    private final SchedulingModeContainer schedulingModeContainer;

    protected AbstractTieredExporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getTieredExporters(tier),
            pos,
            state,
            new TieredExporterNetworkNode(tier.getEnergyUsage(CableType.EXPORTER)),
            tier,
            CableType.EXPORTER);
        this.filter = TagFilterWithFuzzyMode.createAndListenForFilters(
            AdvancedResourceContainerImpl.createForFilter(tier),
            this::setChanged,
            this::setFilters
        );
        this.upgradeContainer = new UpgradeContainer(getUpgradeDestination(tier), (c, upgradeEnergyUsage) -> {
            mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.EXPORTER) + upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        }) {
            @Override
            public boolean has(final UpgradeItem upgradeItem) {
                if (tier.hasIntegratedStackUpgrade(CableType.EXPORTER) && upgradeItem == Items.INSTANCE.getStackUpgrade()) {
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
        this.schedulingModeContainer = new SchedulingModeContainer(this::schedulingModeChanged);
        this.ticker = upgradeContainer.getTicker();
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier == CableTiers.CREATIVE
            ? TieredUpgradeDestinations.EXPORTER_NO_STACK_SPEED
            : tier.hasIntegratedStackUpgrade(CableType.EXPORTER)
            ? TieredUpgradeDestinations.EXPORTER_NO_STACK
            : UpgradeDestinations.EXPORTER;
    }

    private void schedulingModeChanged(final SchedulingMode schedulingMode) {
        mainNetworkNode.setSchedulingMode(schedulingMode);
        setChanged();
    }

    void setFilters(final List<ResourceKey> filters, final List<ResourceTag> tagFilters) {
        mainNetworkNode.setFilters(filters, tagFilters);
    }

    void setSchedulingModeType(final SchedulingModeType type) {
        schedulingModeContainer.setType(type);
    }

    SchedulingModeType getSchedulingModeType() {
        return schedulingModeContainer.getType();
    }

    boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        schedulingModeContainer.writeToTag(tag);
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        schedulingModeContainer.loadFromTag(tag);
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        final ExporterTransferStrategy strategy = createStrategy(level, direction);
        LOGGER.debug("Initialized exporter at {} with strategy {}", worldPosition, strategy);
        mainNetworkNode.setTransferStrategy(strategy);
    }

    private ExporterTransferStrategy createStrategy(final ServerLevel serverLevel, final Direction direction) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = worldPosition.relative(direction);
        final List<ExporterTransferStrategyFactory> factories =
            RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().getAll();
        final Map<Class<? extends ResourceKey>, ExporterTransferStrategy> strategies =
            factories.stream().collect(Collectors.toMap(
                ExporterTransferStrategyFactory::getResourceType,
                factory -> factory.create(
                    serverLevel,
                    sourcePosition,
                    incomingDirection,
                    upgradeContainer,
                    filter.isFuzzyMode()
                )
            ));
        return new CompositeExporterTransferStrategy(strategies);
    }

    @Override
    public ExporterData getMenuData() {
        final ResourceContainer filterContainer = filter.getFilterContainer();
        final ResourceContainerData resourceContainerData = ResourceContainerData.of(filterContainer);
        return new ExporterData(resourceContainerData, getExportingIndicators().getAll());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ExporterData> getMenuCodec() {
        return ExporterData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return overrideName(tier.getContentName(CableType.EXPORTER));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        this.setInContainerMenu(true);

        return new TieredExporterContainerMenu(syncId, player, this, filter.getFilterContainer(), upgradeContainer, getExportingIndicators(), tier);
    }

    private TieredExportingIndicators getExportingIndicators() {
        return new TieredExportingIndicators(
            filter.getFilterContainer(),
            (i, j) -> toExportingIndicator(mainNetworkNode.getLastResult(i, j)),
            false
        );
    }

    private ExportingIndicator toExportingIndicator(@Nullable final ExporterTransferStrategy.Result result) {
        return switch (result) {
            case DESTINATION_DOES_NOT_ACCEPT -> ExportingIndicator.DESTINATION_DOES_NOT_ACCEPT_RESOURCE;
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }
}
