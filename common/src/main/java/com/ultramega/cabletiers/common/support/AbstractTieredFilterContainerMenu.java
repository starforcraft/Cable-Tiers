package com.ultramega.cabletiers.common.support;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceSlot;
import com.ultramega.cabletiers.common.utils.TagFiltering;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public abstract class AbstractTieredFilterContainerMenu<T extends TagFiltering> extends AbstractResourceContainerMenu {
    private static final int FILTER_SLOT_X = 8;
    private static final int FILTER_SLOT_Y = 20;

    protected final CableTiers tier;
    protected int playerInventoryY;

    private final Component filterHelp;

    @Nullable
    private TagFiltering blockEntity;
    @Nullable
    private List<@Nullable ResourceTag> tagKeys;

    protected AbstractTieredFilterContainerMenu(final MenuType<?> type,
                                                final int syncId,
                                                final Player player,
                                                final ResourceContainer resourceContainer,
                                                @Nullable final UpgradeContainer upgradeContainer,
                                                final T blockEntity,
                                                final int playerInventoryY,
                                                final Component filterHelp,
                                                final CableTiers tier) {
        super(type, syncId, player);
        this.filterHelp = filterHelp;
        this.tier = tier;
        this.blockEntity = blockEntity;
        this.playerInventoryY = playerInventoryY;
        this.registerServerProperties(blockEntity);
        this.addSlots(player, resourceContainer, upgradeContainer);

        this.blockEntity.setOnChanged(() -> {
            if (player instanceof ServerPlayer serverPlayer) {
                blockEntity.sendFilterTagsToClient(serverPlayer);
            }
        });
    }

    protected AbstractTieredFilterContainerMenu(final MenuType<?> type,
                                                final int syncId,
                                                final Player player,
                                                final ResourceContainerData resourceContainerData,
                                                @Nullable final UpgradeDestination upgradeDestination,
                                                final int playerInventoryY,
                                                final Component filterHelp,
                                                final CableTiers tier) {
        super(type, syncId);
        this.filterHelp = filterHelp;
        this.tier = tier;
        this.playerInventoryY = playerInventoryY;
        this.registerClientProperties();
        this.addSlots(
            player,
            ResourceContainerImpl.createForFilter(resourceContainerData),
            upgradeDestination == null ? null : new UpgradeContainer(upgradeDestination)
        );
    }

    protected abstract void registerClientProperties();

    protected abstract void registerServerProperties(T blockEntity);

    protected void addSlots(final Player player,
                            final ResourceContainer resourceContainer,
                            @Nullable final UpgradeContainer upgradeContainer) {
        for (int i = 0; i < resourceContainer.size(); ++i) {
            this.addSlot(this.createFilterSlot(resourceContainer, i));
        }
        if (upgradeContainer != null) {
            for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
                this.addSlot(new UpgradeSlot(upgradeContainer, i, 187, 6 + (i * 18)));
            }
        }
        this.addPlayerInventory(player.getInventory(), 8, this.playerInventoryY);

        if (upgradeContainer != null) {
            this.transferManager.addBiTransfer(player.getInventory(), upgradeContainer);
        }
        this.transferManager.addFilterTransfer(player.getInventory());
    }

    private Slot createFilterSlot(final ResourceContainer resourceContainer, final int i) {
        final int x = FILTER_SLOT_X + (18 * (i % 9));
        final int y = FILTER_SLOT_Y + (18 * (i / 9));
        return new AdvancedResourceSlot(this, this.player, resourceContainer, i, this.filterHelp, x, y, ResourceSlotType.FILTER);
    }

    @Override
    public void removed(final Player player) {
        if (this.blockEntity != null) {
            this.blockEntity.setInContainerMenu(false);
            this.blockEntity.resetFakeFilters();
        }
        super.removed(player);
    }

    public void setTagFilter(final int slotIndex, @Nullable final ResourceTag resourceTag) {
        if (this.blockEntity != null) {
            this.blockEntity.setTagFilter(slotIndex, resourceTag);
        }
    }

    @Nullable
    public TagKey<?> getTagFilter(final int slotIndex) {
        if (this.blockEntity != null) {
            return this.blockEntity.getTagFilter(slotIndex);
        }

        return null;
    }

    public void setTagKeys(final List<ResourceTag> tagKeys) {
        this.tagKeys = tagKeys;
    }

    @Nullable
    public List<@Nullable ResourceTag> getTagKeys() {
        return this.tagKeys;
    }
}
