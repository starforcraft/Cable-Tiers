package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.utils.TagsCache;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.DisabledResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import static com.ultramega.cabletiers.common.advancedfilter.AdvancedFilterScreen.ADVANCED_TAG_HEIGHT;
import static com.ultramega.cabletiers.common.advancedfilter.AdvancedFilterScreen.RESOURCES_PER_ROW;
import static com.ultramega.cabletiers.common.advancedfilter.AdvancedFilterScreen.ROW_HEIGHT;

public class AdvancedFilterContainerMenu extends AbstractResourceContainerMenu {
    private final List<AdvancedTag> advancedTags;
    private final ResourceSlot filterSlot;

    protected AdvancedFilterContainerMenu(@Nullable final PlatformResourceKey selectedResource) {
        super(null, 0);

        this.advancedTags = selectedResource != null
            ? TagsCache.get(selectedResource).stream()
            .sorted(Comparator.comparing((ResourceTag tag) -> tag.key().location().getNamespace())
                .thenComparing(tag -> tag.key().location().getPath()))
            .map(AdvancedTag::new).toList()
            : new ArrayList<>();

        final ResourceContainer resourceContainer = ResourceContainerImpl.createForFilter(1);
        if (selectedResource != null) {
            resourceContainer.set(0, new ResourceAmount(selectedResource, 1));
        }
        filterSlot = new DisabledResourceSlot(resourceContainer, 0, Component.empty(), 105, 23, ResourceSlotType.FILTER);
        addSlot(filterSlot);

        addAdvancedTagSlots();
    }

    void filter(final String query) {
        final String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        advancedTags.forEach(tagKey -> {
            final boolean titleMatch = I18n.exists(tagKey.getTranslationKey())
                && I18n.get(tagKey.getTranslationKey()).trim().toLowerCase(Locale.ROOT).contains(normalizedQuery);
            final boolean idMatch = tagKey.getId().toString().trim().toLowerCase(Locale.ROOT)
                .contains(normalizedQuery);
            tagKey.setVisible(titleMatch || idMatch);
        });
    }

    private void addAdvancedTagSlots() {
        final int x = 8;
        for (int i = 0; i < advancedTags.size(); ++i) {
            final int y = 122 + (i * ADVANCED_TAG_HEIGHT);
            final AdvancedTag advancedTag = advancedTags.get(i);
            final ResourceContainer resources = ResourceContainerImpl.createForFilter(
                advancedTag.getResources().size()
            );
            for (int j = 0; j < resources.size(); ++j) {
                resources.set(j, new ResourceAmount(advancedTag.getResources().get(j), 1));
                final int row = j / RESOURCES_PER_ROW;
                final int col = j % RESOURCES_PER_ROW;
                final int slotX = x + 1 + 1 + col * 18;
                final int slotY = y + ROW_HEIGHT + (row * 18) + 1;
                final AdvancedTagSlot resourceSlot = new AdvancedTagSlot(resources, j, slotX, slotY);
                if (j < RESOURCES_PER_ROW) {
                    advancedTag.getMainSlots().add(resourceSlot);
                } else {
                    advancedTag.getOverflowSlots().add(resourceSlot);
                }
                addSlot(resourceSlot);
            }
        }
    }

    public List<AdvancedTag> getAdvancedTags() {
        return advancedTags;
    }

    public ResourceSlot getFilterSlot() {
        return filterSlot;
    }

    @Override
    public boolean stillValid(final Player p) {
        return true;
    }
}
