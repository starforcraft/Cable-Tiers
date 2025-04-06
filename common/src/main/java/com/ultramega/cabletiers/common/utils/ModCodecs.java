package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ModCodecs {
    public static final Codec<TagKey<?>> TAG_KEY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("registry").forGetter(tagKey -> tagKey.registry().location()),
        ResourceLocation.CODEC.fieldOf("tag").forGetter(TagKey::location)
    ).apply(instance, (registry, tag) -> {
        final ResourceKey<Registry<Object>> registryKey = ResourceKey.createRegistryKey(registry);
        return TagKey.create(registryKey, tag);
    }));

    public static final StreamCodec<FriendlyByteBuf, TagKey<?>> TAG_KEY_GENERIC_STREAM_CODEC = StreamCodec.of(
        (buf, tagKey) -> {
            buf.writeResourceLocation(tagKey.registry().location());
            buf.writeResourceLocation(tagKey.location());
        },
        buf -> {
            final ResourceLocation registryLocation = buf.readResourceLocation();
            final ResourceLocation tagLocation = buf.readResourceLocation();

            final ResourceKey<Registry<Object>> registryKey = ResourceKey.createRegistryKey(registryLocation);

            return TagKey.create(registryKey, tagLocation);
        }
    );

    public static final Codec<ResourceTag> RESOURCE_TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TAG_KEY_CODEC.fieldOf("key").forGetter(ResourceTag::key),
        ResourceCodecs.CODEC.listOf().fieldOf("resources").forGetter(ResourceTag::resources)
    ).apply(instance, ResourceTag::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceTag> RESOURCE_TAG_STREAM_CODEC = StreamCodec.composite(
        TAG_KEY_GENERIC_STREAM_CODEC, ResourceTag::key,
        ResourceCodecs.STREAM_CODEC.apply(ByteBufCodecs.list()), ResourceTag::resources,
        ResourceTag::new
    );

    private ModCodecs() {
    }
}
