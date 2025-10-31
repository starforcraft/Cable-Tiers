package com.ultramega.cabletiers.common.support;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.utils.TagFiltering;
import com.ultramega.cabletiers.common.utils.TieredSimpleNetworkNode;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.AbstractCableLikeBlockEntity;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTieredCableLikeBlockEntity<T extends TieredSimpleNetworkNode> extends AbstractCableLikeBlockEntity<T>
    implements BlockEntityWithDrops, TagFiltering {
    protected static final String TAG_FILTER_MODE = "fim";
    protected static final String TAG_UPGRADES = "upgr";

    protected final CableTiers tier;
    protected final CableType type;

    protected TagFilterWithFuzzyMode filter;
    protected UpgradeContainer upgradeContainer;

    @Nullable
    private Runnable onChanged;
    private boolean inContainerMenu;

    protected AbstractTieredCableLikeBlockEntity(final BlockEntityType<?> blockEntityType,
                                                 final BlockPos pos,
                                                 final BlockState state,
                                                 final T networkNode,
                                                 final CableTiers tier,
                                                 final CableType type) {
        super(blockEntityType, pos, state, networkNode);
        this.tier = tier;
        this.type = type;

        mainNetworkNode.setTier(tier);
        mainNetworkNode.setType(type);
    }

    @Override
    public void doWork() {
        super.doWork();

        if (!inContainerMenu) {
            return;
        }

        filter.doWork();
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        filter.save(tag, provider);
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        filter.load(tag, provider);
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
    public final NonNullList<ItemStack> getDrops() {
        return upgradeContainer.getDrops();
    }

    @Override
    public void setTagFilter(final int index, @Nullable final ResourceTag resourceTag) {
        filter.setFilterTag(index, resourceTag);
    }

    @Override
    public @Nullable TagKey<?> getTagFilter(final int index) {
        return filter.getFilterContainer().getFilterTag(index);
    }

    @Override
    public void resetFakeFilters() {
        filter.resetFakeFilters();
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (onChanged != null) {
            onChanged.run();
        }
    }

    @Override
    public void sendFilterTagsToClient(final ServerPlayer player) {
        filter.sendFilterTagsToClient(player);
    }

    @Override
    public void setOnChanged(@Nullable final Runnable onChanged) {
        this.onChanged = onChanged;
    }

    @Override
    public void setInContainerMenu(final boolean inContainerMenu) {
        this.inContainerMenu = inContainerMenu;
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState, final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }
}
