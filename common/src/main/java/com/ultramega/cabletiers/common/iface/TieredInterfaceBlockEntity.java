package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.iface.externalstorage.TieredInterfaceExternalStorageProvider;
import com.ultramega.cabletiers.common.iface.externalstorage.TieredInterfaceExternalStorageProviderImpl;
import com.ultramega.cabletiers.common.mixin.InvokerResourceContainerImpl;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.utils.AbstractResourceContainerContainerAdapter;
import com.ultramega.cabletiers.common.utils.ContentNames;

import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceTransferResult;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.iface.InterfaceData;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicators;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TieredInterfaceBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<TieredInterfaceNetworkNode>
    implements NetworkNodeExtendedMenuProvider<InterfaceData>, BlockEntityWithDrops {
    private static final String TAG_EXPORT_ITEMS = "ei";
    private static final String TAG_UPGRADES = "upgr";

    private final UpgradeContainer upgradeContainer;
    private final FilterWithFuzzyMode filter;
    private final ExportedResourcesContainer exportedResources;
    private final Container exportedResourcesAsContainer;
    private final TieredInterfaceExternalStorageProvider externalStorageProvider;

    private final CableTiers tier;

    public TieredInterfaceBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getTieredInterfaces(tier),
            pos,
            state,
            new TieredInterfaceNetworkNode(tier.getEnergyUsage(CableType.INTERFACE))
        );
        this.tier = tier;
        this.mainNetworkNode.setTransferQuotaProvider((resource) -> TieredInterfaceBlockEntity.getTransferQuota(tier, resource));
        this.upgradeContainer = new UpgradeContainer(1, UpgradeDestinations.INTERFACE, (c, upgradeEnergyUsage) -> {
            final long baseEnergyUsage = tier.getEnergyUsage(CableType.INTERFACE);
            mainNetworkNode.setEnergyUsage(baseEnergyUsage + upgradeEnergyUsage);
            final boolean autocrafting = c.has(Items.INSTANCE.getAutocraftingUpgrade());
            mainNetworkNode.setOnMissingResources(autocrafting
                ? new InterfaceNetworkNode.AutocraftOnMissingResources()
                : InterfaceNetworkNode.OnMissingResources.EMPTY);
            setChanged();
        });
        this.filter = FilterWithFuzzyMode.create(createFilterContainer(tier), this::setChanged);
        this.exportedResources = createExportedResourcesContainer(tier, filter);
        this.exportedResources.setListener(this::setChanged);
        this.mainNetworkNode.setExportState(exportedResources);
        this.exportedResourcesAsContainer = new AbstractResourceContainerContainerAdapter(exportedResources) {
            @Override
            public void setChanged() {
                ((InvokerResourceContainerImpl) exportedResources).cabletiers$changed();
            }
        };
        this.externalStorageProvider = new TieredInterfaceExternalStorageProviderImpl(mainNetworkNode);
    }

    static ResourceContainer createFilterContainer(final CableTiers tier) {
        return new ResourceContainerImpl(
            tier.getInterfaceSlotsCount(),
            (resource) -> TieredInterfaceBlockEntity.getTransferQuota(tier, resource),
            RefinedStorageApi.INSTANCE.getItemResourceFactory(),
            RefinedStorageApi.INSTANCE.getAlternativeResourceFactories()
        );
    }

    static ResourceContainer createFilterContainer(final CableTiers tier, final InterfaceData interfaceData) {
        final ResourceContainer filterContainer = createFilterContainer(tier);
        final ResourceContainerData resourceContainerData = interfaceData.filterContainerData();
        for (int i = 0; i < resourceContainerData.resources().size(); ++i) {
            final int ii = i;
            resourceContainerData.resources().get(i).ifPresent(resource -> filterContainer.set(ii, resource));
        }
        return filterContainer;
    }

    static ExportedResourcesContainer createExportedResourcesContainer(final CableTiers tier, final FilterWithFuzzyMode filter) {
        return new ExportedResourcesContainer(tier, tier.getInterfaceSlotsCount(), filter);
    }

    static ResourceContainer createExportedResourcesContainer(final CableTiers tier, final InterfaceData interfaceData, final FilterWithFuzzyMode filter) {
        final ExportedResourcesContainer exportedResourcesContainer = createExportedResourcesContainer(tier, filter);
        final ResourceContainerData resourceContainerData = interfaceData.exportedResourcesContainerData();
        for (int i = 0; i < resourceContainerData.resources().size(); ++i) {
            final int ii = i;
            resourceContainerData.resources().get(i).ifPresent(resource -> exportedResourcesContainer.set(ii, resource));
        }
        return exportedResourcesContainer;
    }

    static long getTransferQuota(final CableTiers tier, final ResourceKey resource) {
        if (resource instanceof PlatformResourceKey platformResource) {
            final long transferQuotaMultiplier = tier.getTransferQuotaMultiplier(CableType.INTERFACE);
            if (transferQuotaMultiplier == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            } else {
                return platformResource.getInterfaceExportLimit() * transferQuotaMultiplier;
            }
        }
        return 0;
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_EXPORT_ITEMS, exportedResources.toTag(provider));
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        filter.save(tag, provider);
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_EXPORT_ITEMS)) {
            exportedResources.fromTag(tag.getCompound(TAG_EXPORT_ITEMS), provider);
        }
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
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
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        filter.load(tag, provider);
    }

    boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
    }

    void clearFilters() {
        filter.getFilterContainer().clear();
    }

    void setFilters(final List<ResourceAmount> filters) {
        for (int i = 0; i < filters.size(); i++) {
            filter.getFilterContainer().set(i, filters.get(i));
        }
    }

    public ExportedResourcesContainer getExportedResources() {
        return exportedResources;
    }

    public Container getExportedResourcesAsContainer() {
        return exportedResourcesAsContainer;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new TieredInterfaceContainerMenu(
            syncId,
            player,
            this,
            filter.getFilterContainer(),
            exportedResources,
            exportedResourcesAsContainer,
            upgradeContainer,
            getExportingIndicators(),
            tier
        );
    }

    private ExportingIndicators getExportingIndicators() {
        return new ExportingIndicators(
            filter.getFilterContainer(),
            i -> toExportingIndicator(mainNetworkNode.getLastResult(i)),
            true
        );
    }

    private ExportingIndicator toExportingIndicator(@Nullable final InterfaceTransferResult result) {
        return switch (result) {
            case STORAGE_DOES_NOT_ACCEPT_RESOURCE -> ExportingIndicator.DESTINATION_DOES_NOT_ACCEPT_RESOURCE;
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }

    @Override
    public InterfaceData getMenuData() {
        return new InterfaceData(
            ResourceContainerData.of(filter.getFilterContainer()),
            ResourceContainerData.of(exportedResources),
            getExportingIndicators().getAll()
        );
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, InterfaceData> getMenuCodec() {
        return InterfaceData.STREAM_CODEC;
    }

    @Override
    public Component getName() {
        return overrideName(ContentNames.getContentName(tier, CableType.INTERFACE));
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        final NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < exportedResourcesAsContainer.getContainerSize(); ++i) {
            drops.add(exportedResourcesAsContainer.getItem(i));
        }
        drops.addAll(upgradeContainer.getDrops());
        return drops;
    }

    TieredInterfaceExternalStorageProvider getExternalStorageProvider() {
        return externalStorageProvider;
    }

    TieredInterfaceNetworkNode getInterface() {
        return mainNetworkNode;
    }
}
