package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.TieredUpgradeDestinations;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.support.AbstractTieredCableLikeBlockEntity;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.constructordestructor.DestructorStrategy;
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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class AbstractTieredDestructorBlockEntity extends AbstractTieredCableLikeBlockEntity<TieredDestructorNetworkNode>
    implements NetworkNodeExtendedMenuProvider<ResourceContainerData> {
    private static final String TAG_PICKUP_ITEMS = "pi";

    private boolean pickupItems;

    protected AbstractTieredDestructorBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getTieredDestructors(tier),
            pos,
            state,
            new TieredDestructorNetworkNode(tier.getEnergyUsage(CableType.DESTRUCTOR)),
            tier,
            CableType.DESTRUCTOR);
        this.filter = TagFilterWithFuzzyMode.createAndListenForUniqueFilters(
            AdvancedResourceContainerImpl.createForFilter(tier),
            this::setChanged,
            this::setFilters
        );
        this.upgradeContainer = new UpgradeContainer(getUpgradeDestination(tier), (c, upgradeEnergyUsage) -> {
            this.mainNetworkNode.setEnergyUsage(tier.getEnergyUsage(CableType.DESTRUCTOR) + upgradeEnergyUsage);
            if (this.level instanceof ServerLevel serverLevel) {
                this.initialize(serverLevel);
            }
        }, this::setChanged, Math.max(1, 20 - tier.getSpeed(CableType.DESTRUCTOR))) {
            @Override
            public int getAmount(final UpgradeItem upgradeItem) {
                if (tier == CableTiers.CREATIVE && upgradeItem == Items.INSTANCE.getSpeedUpgrade()) {
                    return 4;
                }
                return super.getAmount(upgradeItem);
            }
        };
        this.ticker = this.upgradeContainer.getTicker();
    }

    public static UpgradeDestination getUpgradeDestination(final CableTiers tier) {
        return tier == CableTiers.CREATIVE
            ? TieredUpgradeDestinations.DESTRUCTOR_NO_SPEED
            : UpgradeDestinations.DESTRUCTOR;
    }

    public boolean isPickupItems() {
        return this.pickupItems;
    }

    public void setPickupItems(final boolean pickupItems) {
        this.pickupItems = pickupItems;
        this.setChanged();
        if (this.level instanceof ServerLevel serverLevel) {
            this.initialize(serverLevel);
        }
    }

    void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        this.mainNetworkNode.setFilters(filters, tagFilters);
    }

    public FilterMode getFilterMode() {
        return this.mainNetworkNode.getFilterMode();
    }

    public void setFilterMode(final FilterMode mode) {
        this.mainNetworkNode.setFilterMode(mode);
        this.setChanged();
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        output.putInt(TAG_FILTER_MODE, FilterModeSettings.getFilterMode(this.mainNetworkNode.getFilterMode()));
        output.putBoolean(TAG_PICKUP_ITEMS, this.pickupItems);
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        input.getInt(TAG_FILTER_MODE).map(FilterModeSettings::getFilterMode).ifPresent(this.mainNetworkNode::setFilterMode);
        this.pickupItems = input.getBooleanOr(TAG_PICKUP_ITEMS, false);
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        this.mainNetworkNode.setPlayerProvider(() -> this.getFakePlayer(level));
        this.mainNetworkNode.setStrategy(this.createStrategy(level, direction));
    }

    private CompositeDestructorStrategy createStrategy(final ServerLevel level,
                                                       final Direction direction) {
        final BlockPos pos = this.getBlockPos().relative(direction);
        final Direction incomingDirection = direction.getOpposite();
        final List<DestructorStrategy> strategies = RefinedStorageApi.INSTANCE.getDestructorStrategyFactories()
            .stream()
            .flatMap(factory -> factory.create(level, pos, incomingDirection, this.upgradeContainer, this.pickupItems).stream())
            .toList();
        return new CompositeDestructorStrategy(strategies);
    }

    @Override
    public ResourceContainerData getMenuData() {
        return ResourceContainerData.of(this.filter.getFilterContainer());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ResourceContainerData> getMenuCodec() {
        return ResourceContainerData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return this.overrideName(this.tier.getContentName(CableType.DESTRUCTOR));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        this.setInContainerMenu(true);

        return new TieredDestructorContainerMenu(syncId, player, this, this.filter.getFilterContainer(), this.upgradeContainer, this.tier);
    }
}
