package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.TieredUpgradeDestinations;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.exporters.TieredExportingIndicators;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.support.AbstractTieredCableLikeBlockEntity;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategyFactory;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeItem;
import com.refinedmods.refinedstorage.common.constructordestructor.ConstructorData;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.SchedulingModeContainer;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;

import java.util.Collection;
import java.util.List;
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

public class AbstractTieredConstructorBlockEntity extends AbstractTieredCableLikeBlockEntity<TieredConstructorNetworkNode>
    implements NetworkNodeExtendedMenuProvider<ConstructorData> {
    private static final String TAG_DROP_ITEMS = "di";

    private final SchedulingModeContainer schedulingModeContainer;

    private boolean dropItems;

    protected AbstractTieredConstructorBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getTieredConstructors(tier),
            pos,
            state,
            new TieredConstructorNetworkNode(tier.getEnergyUsage(CableType.CONSTRUCTOR)),
            tier,
            CableType.CONSTRUCTOR);
        this.filter = TagFilterWithFuzzyMode.createAndListenForFilters(
            AdvancedResourceContainerImpl.createForFilter(tier),
            this::setChanged,
            this::setFilters
        );
        this.upgradeContainer = new UpgradeContainer(getUpgradeDestination(tier), (c, upgradeEnergyUsage) -> {
            mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.CONSTRUCTOR) + upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        }, Math.max(1, 20 - tier.getSpeed(CableType.CONSTRUCTOR))) {
            @Override
            public boolean has(final UpgradeItem upgradeItem) {
                if (tier.hasIntegratedStackUpgrade(CableType.CONSTRUCTOR) && upgradeItem == Items.INSTANCE.getStackUpgrade()) {
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
        this.schedulingModeContainer = new SchedulingModeContainer(schedulingMode -> {
            mainNetworkNode.setSchedulingMode(schedulingMode);
            setChanged();
        });
        this.ticker = upgradeContainer.getTicker();
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier == CableTiers.CREATIVE
            ? TieredUpgradeDestinations.CONSTRUCTOR_NO_STACK_SPEED
            : tier.hasIntegratedStackUpgrade(CableType.CONSTRUCTOR)
            ? TieredUpgradeDestinations.CONSTRUCTOR_NO_STACK
            : UpgradeDestinations.CONSTRUCTOR;
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

    boolean isDropItems() {
        return dropItems;
    }

    void setDropItems(final boolean dropItems) {
        this.dropItems = dropItems;
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        tag.putBoolean(TAG_DROP_ITEMS, dropItems);
        schedulingModeContainer.writeToTag(tag);
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        if (tag.contains(TAG_DROP_ITEMS)) {
            dropItems = tag.getBoolean(TAG_DROP_ITEMS);
        }
        schedulingModeContainer.loadFromTag(tag);
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        mainNetworkNode.setPlayerProvider(() -> getFakePlayer(level));
        mainNetworkNode.setStrategy(createStrategy(level, direction));
    }

    private ConstructorStrategy createStrategy(final ServerLevel serverLevel, final Direction direction) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = worldPosition.relative(direction);
        final Collection<ConstructorStrategyFactory> factories = RefinedStorageApi.INSTANCE
            .getConstructorStrategyFactories();
        final List<ConstructorStrategy> strategies = factories.stream().flatMap(factory -> factory.create(
                serverLevel,
                sourcePosition,
                incomingDirection,
                upgradeContainer,
                dropItems
            ).stream())
            .toList();
        final ConstructorStrategy strategy = new CompositeConstructorStrategy(strategies);
        if (upgradeContainer.has(Items.INSTANCE.getAutocraftingUpgrade())) {
            return new AutocraftOnMissingResourcesConstructorStrategy(strategy);
        }
        return strategy;
    }

    @Override
    public ConstructorData getMenuData() {
        return new ConstructorData(ResourceContainerData.of(filter.getFilterContainer()),
            getExportingIndicators().getAll());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ConstructorData> getMenuCodec() {
        return ConstructorData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return overrideName(tier.getContentName(CableType.CONSTRUCTOR));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        this.setInContainerMenu(true);

        return new TieredConstructorContainerMenu(syncId, player, this, filter.getFilterContainer(), upgradeContainer, getExportingIndicators(), tier);
    }

    private TieredExportingIndicators getExportingIndicators() {
        return new TieredExportingIndicators(
            filter.getFilterContainer(),
            (i, j) -> toExportingIndicator(mainNetworkNode.getLastResult(i, j)),
            false
        );
    }

    private ExportingIndicator toExportingIndicator(@Nullable final ConstructorStrategy.Result result) {
        return switch (result) {
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }
}
