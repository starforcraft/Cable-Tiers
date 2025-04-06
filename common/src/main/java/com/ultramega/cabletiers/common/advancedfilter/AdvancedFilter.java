package com.ultramega.cabletiers.common.advancedfilter;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.Filter;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import net.minecraft.tags.TagKey;

public class AdvancedFilter extends Filter {
    private final Set<ResourceKey> itemFilters = new HashSet<>();
    private final Set<TagKey<?>> tagFilters = new HashSet<>();

    private FilterMode mode = FilterMode.BLOCK;
    private UnaryOperator<ResourceKey> normalizer = value -> value;

    @Override
    public FilterMode getMode() {
        return mode;
    }

    @Override
    public void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        this.normalizer = normalizer;
    }

    @Override
    public void setMode(final FilterMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean isAllowed(final ResourceKey resource) {
        final ResourceKey normalized = normalizer.apply(resource);

        if (normalized instanceof PlatformResourceKey platformResourceKey) {
            boolean foundTag = false;

            final List<ResourceTag> tags = platformResourceKey.getTags();
            for (final ResourceTag tag : tags) {
                if (tagFilters.contains(tag.key())) {
                    foundTag = true;
                    break;
                }
            }

            final boolean isAllowed = !(foundTag || itemFilters.contains(normalized));

            return switch (mode) {
                case ALLOW -> !isAllowed;
                case BLOCK -> isAllowed;
            };
        }

        return false;
    }

    @Override
    public void setFilters(final Set<ResourceKey> filters) {
        this.itemFilters.clear();
        this.itemFilters.addAll(filters.stream().map(normalizer).collect(Collectors.toSet()));
    }

    public void setTagFilters(final Set<TagKey<?>> tagFilters) {
        this.tagFilters.clear();
        this.tagFilters.addAll(new HashSet<>(tagFilters));
    }
}
