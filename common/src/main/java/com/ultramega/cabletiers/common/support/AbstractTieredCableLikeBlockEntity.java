package com.ultramega.cabletiers.common.support;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode;
import com.ultramega.cabletiers.common.utils.TagFiltering;
import com.ultramega.cabletiers.common.utils.TieredSimpleNetworkNode;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.AbstractCableLikeBlockEntity;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class AbstractTieredCableLikeBlockEntity<T extends TieredSimpleNetworkNode> extends AbstractCableLikeBlockEntity<T> implements TagFiltering {
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

        this.mainNetworkNode.setTier(tier);
        this.mainNetworkNode.setType(type);
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
    public void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_UPGRADES, ItemContainerContents.CODEC, ItemContainerContents.fromItems(this.upgradeContainer.getItems()));
    }

    @Override
    public void loadAdditional(final ValueInput input) {
        input.read(TAG_UPGRADES, ItemContainerContents.CODEC).ifPresent(this.upgradeContainer::load);
        super.loadAdditional(input);
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        this.filter.store(output);
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        this.filter.read(input);
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
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this.upgradeContainer.getDrops());
        }
    }

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
    public void setChanged() {
        super.setChanged();

        if (this.onChanged != null) {
            this.onChanged.run();
        }
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

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState, final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }
}
