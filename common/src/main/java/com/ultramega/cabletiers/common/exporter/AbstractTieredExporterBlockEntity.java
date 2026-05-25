package com.ultramega.cabletiers.common.exporter;

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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
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
            this.mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.EXPORTER) + upgradeEnergyUsage);
            if (this.level instanceof ServerLevel serverLevel) {
                this.initialize(serverLevel);
            }
        }, this::setChanged) {
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
        this.schedulingModeContainer = new SchedulingModeContainer(this.mainNetworkNode::setSchedulingMode, this::setChanged);
        this.ticker = this.upgradeContainer.getTicker();
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier == CableTiers.CREATIVE
            ? TieredUpgradeDestinations.EXPORTER_NO_STACK_SPEED
            : tier.hasIntegratedStackUpgrade(CableType.EXPORTER)
            ? TieredUpgradeDestinations.EXPORTER_NO_STACK
            : UpgradeDestinations.EXPORTER;
    }

    private void schedulingModeChanged(final SchedulingMode schedulingMode) {
        this.mainNetworkNode.setSchedulingMode(schedulingMode);
        this.setChanged();
    }

    void setFilters(final List<ResourceKey> filters, final List<ResourceTag> tagFilters) {
        this.mainNetworkNode.setFilters(filters, tagFilters);
    }

    void setSchedulingModeType(final SchedulingModeType type) {
        this.schedulingModeContainer.setType(type);
    }

    SchedulingModeType getSchedulingModeType() {
        return this.schedulingModeContainer.getType();
    }

    boolean isFuzzyMode() {
        return this.filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        this.filter.setFuzzyMode(fuzzyMode);
        if (this.level instanceof ServerLevel serverLevel) {
            this.initialize(serverLevel);
        }
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        this.schedulingModeContainer.store(output);
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        this.schedulingModeContainer.read(input);
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        final ExporterTransferStrategy strategy = this.createStrategy(level, direction);
        LOGGER.debug("Initialized exporter at {} with strategy {}", this.worldPosition, strategy);
        this.mainNetworkNode.setTransferStrategy(strategy);
    }

    private ExporterTransferStrategy createStrategy(final ServerLevel serverLevel, final Direction direction) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = this.worldPosition.relative(direction);
        final List<ExporterTransferStrategyFactory> factories =
            RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().getAll();
        final Map<Class<? extends ResourceKey>, ExporterTransferStrategy> strategies =
            factories.stream().collect(Collectors.toMap(
                ExporterTransferStrategyFactory::getResourceType,
                factory -> factory.create(
                    serverLevel,
                    sourcePosition,
                    incomingDirection,
                    this.upgradeContainer,
                    this.filter.isFuzzyMode()
                )
            ));
        return new CompositeExporterTransferStrategy(strategies);
    }

    @Override
    public ExporterData getMenuData() {
        final ResourceContainer filterContainer = this.filter.getFilterContainer();
        final ResourceContainerData resourceContainerData = ResourceContainerData.of(filterContainer);
        return new ExporterData(resourceContainerData, this.getExportingIndicators().getAll());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ExporterData> getMenuCodec() {
        return ExporterData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return this.overrideName(this.tier.getContentName(CableType.EXPORTER));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        this.setInContainerMenu(true);

        return new TieredExporterContainerMenu(syncId, player, this, this.filter.getFilterContainer(), this.upgradeContainer, this.getExportingIndicators(), this.tier);
    }

    private TieredExportingIndicators getExportingIndicators() {
        return new TieredExportingIndicators(
            this.filter.getFilterContainer(),
            (i, j) -> this.toExportingIndicator(this.mainNetworkNode.getLastResult(i, j)),
            false
        );
    }

    private ExportingIndicator toExportingIndicator(final ExporterTransferStrategy.@Nullable Result result) {
        return switch (result) {
            case DESTINATION_DOES_NOT_ACCEPT -> ExportingIndicator.DESTINATION_DOES_NOT_ACCEPT_RESOURCE;
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }
}
