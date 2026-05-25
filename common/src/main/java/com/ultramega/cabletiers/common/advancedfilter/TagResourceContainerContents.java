package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.utils.ModCodecs;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainerContents;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TagResourceContainerContents(ResourceContainerContents resourceContents, List<Optional<ResourceTag>> filterTags, List<Integer> fakeStartIndices) {
    public static final Codec<TagResourceContainerContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceCodecs.CONTAINER_CONTENTS_CODEC.fieldOf("resourceContents").forGetter(TagResourceContainerContents::resourceContents),
        ModCodecs.OPTIONAL_RESOURCE_TAG_CODEC.listOf().fieldOf("filterTags").forGetter(TagResourceContainerContents::filterTags),
        Codec.INT.listOf().fieldOf("fakeStartIndices").forGetter(TagResourceContainerContents::fakeStartIndices)
    ).apply(instance, TagResourceContainerContents::new));

    public static TagResourceContainerContents of(final AdvancedResourceContainerImpl container) {
        return new TagResourceContainerContents(
            ResourceContainerContents.of(container),
            container.getFilterTagsWithNull()
                .stream()
                .map(Optional::ofNullable)
                .toList(),
            container.getFakeStartIndices()
        );
    }
}
