package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class TagsCache {
    private static final Object2ObjectOpenHashMap<PlatformResourceKey, List<ResourceTag>> CACHE = new Object2ObjectOpenHashMap<>();

    private TagsCache() {
    }

    public static List<ResourceTag> get(final PlatformResourceKey resource) {
        return CACHE.computeIfAbsent(resource, TagsCache::computeTags);
    }

    public static void invalidateAll() {
        CACHE.clear();
    }

    private static List<ResourceTag> computeTags(final PlatformResourceKey resource) {
        return Collections.unmodifiableList(resource.getTags());
    }
}
