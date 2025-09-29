package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.packet.s2c.UpdateAdvancedFilterPacket;
import com.ultramega.cabletiers.common.utils.TagsCache;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.FuzzyModeNormalizer;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;

public final class TagFilterWithFuzzyMode {
    private static final String TAG_FUZZY_MODE = "fm";
    private static final String TAG_RESOURCE_FILTER = "rf";
    private static final int ANIMATION_COOLDOWN = 20;

    private final AdvancedResourceContainerImpl filterContainer;
    @Nullable
    private final Runnable listener;
    @Nullable
    private final BiConsumer<Set<ResourceKey>, Set<TagKey<?>>> uniqueFilterListener;
    @Nullable
    private final BiConsumer<List<ResourceKey>, List<ResourceTag>> filterListener;

    private int animationTick;

    private boolean fuzzyMode;

    private TagFilterWithFuzzyMode(final AdvancedResourceContainerImpl filterContainer,
                                   @Nullable final Runnable listener,
                                   @Nullable final BiConsumer<Set<ResourceKey>, Set<TagKey<?>>> uniqueFilterListener,
                                   @Nullable final BiConsumer<List<ResourceKey>, List<ResourceTag>> filterListener) {
        this.filterContainer = filterContainer;
        this.listener = listener;
        this.uniqueFilterListener = uniqueFilterListener;
        this.filterListener = filterListener;
        this.filterContainer.setListener((index) -> this.filterContainerChanged(index, true));
    }

    public void doWork() {
        if (++animationTick > ANIMATION_COOLDOWN) {
            animationTick = 0;

            for (int i = 0; i < filterContainer.getFilterTagsWithNull().size(); i++) {
                updateShowcasedItem(i);
            }
        }
    }

    private void updateShowcasedItem(final int index) {
        final TagKey<?> filterTagKey = filterContainer.getFilterTag(index);
        if (filterTagKey == null) {
            return;
        }

        final ResourceAmount resourceAmount = filterContainer.get(index);
        if (resourceAmount == null) {
            return;
        }

        if (resourceAmount.resource() instanceof PlatformResourceKey platformResourceKey) {
            for (final ResourceTag tag : TagsCache.get(platformResourceKey)) {
                if (!tag.key().equals(filterTagKey)) {
                    continue;
                }

                filterContainer.updateFakeSlot(index, tag);
            }
        }
    }

    private void filterContainerChanged(final Integer index, final boolean overwriteFilterTag) {
        filterContainer.setFakeShowcaseIndex(index, -1);
        filterContainer.setFake(index, null);
        if (overwriteFilterTag) {
            filterContainer.setFilterTag(index, null);
        }

        notifyListeners();
        if (listener != null) {
            listener.run();
        }
    }

    public AdvancedResourceContainerImpl getFilterContainer() {
        return filterContainer;
    }

    public void resetFakeFilters() {
        filterContainer.resetFakeFilters();
    }

    public void sendFilterTagsToClient(final ServerPlayer player) {
        final List<Optional<ResourceTag>> optionalTags = new ArrayList<>();
        for (final ResourceTag resourceTag : filterContainer.getFilterTagsWithNull()) {
            optionalTags.add(Optional.ofNullable(resourceTag));
        }

        Platform.INSTANCE.sendPacketToClient(player, new UpdateAdvancedFilterPacket(optionalTags));
    }

    public void setFilterTag(final int index, @Nullable final ResourceTag resourceTag) {
        filterContainer.setFilterTag(index, resourceTag);

        if (resourceTag != null) {
            final ResourceKey resourceInContainer = filterContainer.getResource(index);
            for (int i = 0; i < resourceTag.resources().size(); i++) {
                final ResourceKey resource = resourceTag.resources().get(i);
                if (resource.equals(resourceInContainer)) {
                    filterContainer.setFakeStartIndex(index, i);
                }
            }
        }

        filterContainerChanged(index, false);
    }

    public boolean isFuzzyMode() {
        return fuzzyMode;
    }

    public void setFuzzyMode(final boolean fuzzyMode) {
        this.fuzzyMode = fuzzyMode;
        // We need to reload the filters as the normalizer will give different outputs now.
        notifyListeners();
        if (listener != null) {
            listener.run();
        }
    }

    public void load(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_RESOURCE_FILTER)) {
            filterContainer.fromTag(tag.getCompound(TAG_RESOURCE_FILTER), provider);
        }
        if (tag.contains(TAG_FUZZY_MODE)) {
            fuzzyMode = tag.getBoolean(TAG_FUZZY_MODE);
        }
        notifyListeners();
    }

    public void save(final CompoundTag tag, final HolderLookup.Provider provider) {
        tag.putBoolean(TAG_FUZZY_MODE, fuzzyMode);
        tag.put(TAG_RESOURCE_FILTER, filterContainer.toTag(provider));
    }

    private void notifyListeners() {
        if (uniqueFilterListener != null) {
            uniqueFilterListener.accept(filterContainer.getUniqueResources(), filterContainer.getUniqueFilterTags());
        }
        if (filterListener != null) {
            filterListener.accept(filterContainer.getResources(), filterContainer.getFilterTags());
        }
    }

    public static List<ResourceKey> getResourcesFromFilter(final List<ResourceKey> filters,
                                                           final List<ResourceTag> tagFilters,
                                                           final int index) {
        final ResourceKey filter = filters.get(index);
        final ResourceTag tagFilter = tagFilters.size() > index ? tagFilters.get(index) : null;

        final List<ResourceKey> resources = new ArrayList<>();
        if (tagFilter != null && !tagFilter.resources().isEmpty()) {
            resources.addAll(tagFilter.resources());
        } else {
            resources.add(filter);
        }

        return resources;
    }

    public UnaryOperator<ResourceKey> createNormalizer() {
        return value -> {
            if (!fuzzyMode) {
                return value;
            }
            if (value instanceof FuzzyModeNormalizer normalizer) {
                return normalizer.normalize();
            }
            return value;
        };
    }

    public static TagFilterWithFuzzyMode create(final AdvancedResourceContainerImpl resourceContainer,
                                                @Nullable final Runnable listener) {
        return new TagFilterWithFuzzyMode(resourceContainer, listener, null, null);
    }

    public static TagFilterWithFuzzyMode createAndListenForFilters(
        final AdvancedResourceContainerImpl resourceContainer,
        final Runnable listener,
        final BiConsumer<List<ResourceKey>, List<ResourceTag>> filterListener) {
        return new TagFilterWithFuzzyMode(resourceContainer, listener, null, filterListener);
    }

    public static TagFilterWithFuzzyMode createAndListenForUniqueFilters(
        final AdvancedResourceContainerImpl resourceContainer,
        final Runnable listener,
        final BiConsumer<Set<ResourceKey>, Set<TagKey<?>>> filterListener) {
        return new TagFilterWithFuzzyMode(resourceContainer, listener, filterListener, null);
    }
}
