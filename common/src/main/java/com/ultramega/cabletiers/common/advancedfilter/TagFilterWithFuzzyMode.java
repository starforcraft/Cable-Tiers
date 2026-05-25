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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

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
        if (++this.animationTick > ANIMATION_COOLDOWN) {
            this.animationTick = 0;

            for (int i = 0; i < this.filterContainer.getFilterTagsWithNull().size(); i++) {
                this.updateShowcasedItem(i);
            }
        }
    }

    private void updateShowcasedItem(final int index) {
        final TagKey<?> filterTagKey = this.filterContainer.getFilterTag(index);
        if (filterTagKey == null) {
            return;
        }

        final ResourceAmount resourceAmount = this.filterContainer.get(index);
        if (resourceAmount == null) {
            return;
        }

        if (resourceAmount.resource() instanceof PlatformResourceKey platformResourceKey) {
            for (final ResourceTag tag : TagsCache.get(platformResourceKey)) {
                if (!tag.key().equals(filterTagKey)) {
                    continue;
                }

                this.filterContainer.updateFakeSlot(index, tag);
            }
        }
    }

    private void filterContainerChanged(final Integer index, final boolean overwriteFilterTag) {
        this.filterContainer.setFakeShowcaseIndex(index, -1);
        this.filterContainer.setFake(index, null);
        if (overwriteFilterTag) {
            this.filterContainer.setFilterTag(index, null);
        }

        this.notifyListeners(true);
    }

    public AdvancedResourceContainerImpl getFilterContainer() {
        return this.filterContainer;
    }

    public void resetFakeFilters() {
        this.filterContainer.resetFakeFilters();
    }

    public void sendFilterTagsToClient(final ServerPlayer player) {
        final List<Optional<ResourceTag>> optionalTags = new ArrayList<>();
        for (final ResourceTag resourceTag : this.filterContainer.getFilterTagsWithNull()) {
            optionalTags.add(Optional.ofNullable(resourceTag));
        }

        Platform.INSTANCE.sendPacketToClient(player, new UpdateAdvancedFilterPacket(optionalTags));
    }

    public void setFilterTag(final int index, @Nullable final ResourceTag resourceTag) {
        this.filterContainer.setFilterTag(index, resourceTag);

        if (resourceTag != null) {
            final ResourceKey resourceInContainer = this.filterContainer.getResource(index);
            for (int i = 0; i < resourceTag.resources().size(); i++) {
                final ResourceKey resource = resourceTag.resources().get(i);
                if (resource.equals(resourceInContainer)) {
                    this.filterContainer.setFakeStartIndex(index, i);
                }
            }
        }

        this.filterContainerChanged(index, false);
    }

    public boolean isFuzzyMode() {
        return this.fuzzyMode;
    }

    public void setFuzzyMode(final boolean fuzzyMode) {
        this.fuzzyMode = fuzzyMode;
        // We need to reload the filters as the normalizer will give different outputs now.
        this.notifyListeners(true);
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
            if (!this.fuzzyMode) {
                return value;
            }
            if (value instanceof FuzzyModeNormalizer normalizer) {
                return normalizer.normalize();
            }
            return value;
        };
    }

    public void store(final ValueOutput output) {
        output.putBoolean(TAG_FUZZY_MODE, this.fuzzyMode);
        output.store(TAG_RESOURCE_FILTER, TagResourceContainerContents.CODEC, TagResourceContainerContents.of(this.filterContainer));
    }

    public void read(final ValueInput input) {
        this.fuzzyMode = input.getBooleanOr(TAG_FUZZY_MODE, false);
        input.read(TAG_RESOURCE_FILTER, TagResourceContainerContents.CODEC).ifPresent(this.filterContainer::load);
        this.notifyListeners(false);
    }

    private void notifyListeners(final boolean changed) {
        if (this.uniqueFilterListener != null) {
            this.uniqueFilterListener.accept(this.filterContainer.getUniqueResources(), this.filterContainer.getUniqueFilterTags());
        }
        if (this.filterListener != null) {
            this.filterListener.accept(this.filterContainer.getResources(), this.filterContainer.getFilterTags());
        }
        if (changed && this.listener != null) {
            this.listener.run();
        }
    }

    public static TagFilterWithFuzzyMode create(final AdvancedResourceContainerImpl resourceContainer,
                                                @Nullable final Runnable listener) {
        return new TagFilterWithFuzzyMode(resourceContainer, listener, null, null);
    }

    public static TagFilterWithFuzzyMode createAndListenForFilters(final AdvancedResourceContainerImpl resourceContainer,
                                                                   final Runnable changeListener,
                                                                   final BiConsumer<List<ResourceKey>, List<ResourceTag>> listener) {
        return new TagFilterWithFuzzyMode(resourceContainer, changeListener, null, listener);
    }

    public static TagFilterWithFuzzyMode createAndListenForUniqueFilters(final AdvancedResourceContainerImpl resourceContainer,
                                                                         final Runnable changeListener,
                                                                         final BiConsumer<Set<ResourceKey>, Set<TagKey<?>>> listener) {
        return new TagFilterWithFuzzyMode(resourceContainer, changeListener, listener, null);
    }
}
