package com.ultramega.cabletiers.common.storage;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.utils.TagFiltering;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractStorageContainerNetworkNode;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.common.storage.DiskInventory;
import com.refinedmods.refinedstorage.common.storage.DiskStateChangeListener;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.util.PlatformUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class AbstractTieredDiskContainerBlockEntity<T extends AbstractStorageContainerNetworkNode> extends AbstractBaseNetworkNodeContainerBlockEntity<T>
    implements NetworkNodeExtendedMenuProvider<ResourceContainerData>, TagFiltering {
    private static final String TAG_DISK_INVENTORY = "inv";
    private static final String TAG_DISKS = "disks";

    protected final CableTiers tier;
    protected final CableType type;

    protected final TagFilterWithFuzzyMode filter;
    protected final DiskInventory diskInventory;
    protected Disk @Nullable [] disks;

    private final DiskStateChangeListener diskStateListener = new DiskStateChangeListener(this);

    @Nullable
    private Runnable onChanged;
    private boolean inContainerMenu;

    protected AbstractTieredDiskContainerBlockEntity(final BlockEntityType<?> blockEntityType,
                                                     final BlockPos pos,
                                                     final BlockState state,
                                                     final T node,
                                                     final CableTiers tier,
                                                     final CableType type) {
        super(blockEntityType, pos, state, node);
        this.tier = tier;
        this.type = type;
        this.diskInventory = new DiskInventory(inv -> this.onDiskChanged(), this.mainNetworkNode.getSize());
        this.filter = TagFilterWithFuzzyMode.createAndListenForUniqueFilters(
            AdvancedResourceContainerImpl.createForFilter(tier),
            this::setChanged,
            this::setFilters
        );
        this.mainNetworkNode.setListener(this.diskStateListener);
        this.setNormalizer(this.filter.createNormalizer());
    }

    @Override
    public void doWork() {
        super.doWork();

        if (!this.inContainerMenu) {
            return;
        }

        this.filter.doWork();
    }

    @Override
    protected void containerInitialized() {
        super.containerInitialized();
        // It's important to sync here as the initial update packet might have failed as the network
        // could possibly be not initialized yet.
        PlatformUtil.sendBlockUpdateToClient(this.level, this.worldPosition);
    }

    protected abstract void setFilters(Set<ResourceKey> filters, Set<TagKey<?>> tagFilters);

    protected abstract void setNormalizer(UnaryOperator<ResourceKey> normalizer);

    @Override
    public void setTagFilter(final int index, @Nullable final ResourceTag resourceTag) {
        this.filter.setFilterTag(index, resourceTag);
    }

    @Override
    public @Nullable TagKey<?> getTagFilter(final int index) {
        return this.filter.getFilterContainer().getFilterTag(index);
    }

    @Override
    public void resetFakeFilters() {
        this.filter.resetFakeFilters();
    }

    @Override
    public void sendFilterTagsToClient(final ServerPlayer player) {
        this.filter.sendFilterTagsToClient(player);
    }

    @Override
    public void setOnChanged(@Nullable final Runnable onChanged) {
        this.onChanged = onChanged;
    }

    @Override
    public void setInContainerMenu(final boolean inContainerMenu) {
        this.inContainerMenu = inContainerMenu;
    }

    void updateDiskStateIfNecessaryInLevel() {
        this.diskStateListener.updateIfNecessary();
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (!level.isClientSide()) {
            this.initialize(level);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.onChanged != null) {
            this.onChanged.run();
        }
    }

    private void initialize(final Level level) {
        this.diskInventory.setStorageRepository(RefinedStorageApi.INSTANCE.getStorageRepository(level));
        this.mainNetworkNode.setProvider(this.diskInventory);
    }

    @Override
    public void activenessChanged(final boolean newActive) {
        super.activenessChanged(newActive);
        PlatformUtil.sendBlockUpdateToClient(this.level, this.worldPosition);
    }

    @Override
    public void loadAdditional(final ValueInput input) {
        this.fromClientTag(input);
        input.read(TAG_DISK_INVENTORY, ItemContainerContents.CODEC).ifPresent(contents -> contents.copyInto(this.diskInventory.getItems()));
        final boolean wasPlacedDismantled = this.level != null && !this.level.isClientSide();
        if (wasPlacedDismantled) {
            this.initialize(this.level);
        }
        super.loadAdditional(input);
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        this.filter.read(input);
    }

    @Override
    public void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_DISK_INVENTORY, ItemContainerContents.CODEC, ItemContainerContents.fromItems(this.diskInventory.getItems()));
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        this.filter.store(output);
    }

    public FilteredContainer getDiskInventory() {
        return this.diskInventory;
    }

    private void onDiskChanged() {
        this.mainNetworkNode.onStorageChanged();
        PlatformUtil.sendBlockUpdateToClient(this.level, this.worldPosition);
        this.setChanged();
    }

    private void fromClientTag(final ValueInput input) {
        input.read(TAG_DISKS, Disk.LIST_CODEC).ifPresent(d -> {
            this.disks = d.toArray(new Disk[0]);
            this.onClientDriveStateUpdated();
        });
    }

    protected void onClientDriveStateUpdated() {
        Platform.INSTANCE.requestModelDataUpdateOnClient(this, true);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        final CompoundTag tag = super.getUpdateTag(registries);
        // This null check is important. #getUpdateTag() can be called before the node's network is initialized!
        if (this.mainNetworkNode.getNetwork() == null) {
            return tag;
        }
        final List<Disk> diskState = new ArrayList<>();
        for (int i = 0; i < this.diskInventory.getContainerSize(); ++i) {
            final ItemStack diskItem = this.diskInventory.getItem(i);
            diskState.add(new Disk(diskItem.isEmpty() ? null : diskItem.getItem(), this.mainNetworkNode.getState(i)));
        }
        tag.store(TAG_DISKS, Disk.LIST_CODEC, diskState);
        return tag;
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this.diskInventory.getItems());
        }
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
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    public static List<@Nullable Item> getDisks(final CompoundTag tag, final int size) {
        final List<@Nullable Item> disks = new ArrayList<>(size);
        tag.read(TAG_DISK_INVENTORY, ItemContainerContents.CODEC).ifPresent(contents -> {
            final NonNullList<ItemStack> inventoryContents = NonNullList.withSize(size, ItemStack.EMPTY);
            contents.copyInto(inventoryContents);
            for (int i = 0; i < size; ++i) {
                disks.add(inventoryContents.get(i).isEmpty() ? null : inventoryContents.get(i).getItem());
            }
        });
        return disks;
    }
}
