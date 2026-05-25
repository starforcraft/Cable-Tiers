package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public final class ModCodecs {
    public static final Codec<TagKey<?>> TAG_KEY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("registry").forGetter(tagKey -> tagKey.registry().identifier()),
        Identifier.CODEC.fieldOf("tag").forGetter(TagKey::location)
    ).apply(instance, (registry, tag) -> {
        final ResourceKey<Registry<Object>> registryKey = ResourceKey.createRegistryKey(registry);
        return TagKey.create(registryKey, tag);
    }));

    public static final StreamCodec<FriendlyByteBuf, TagKey<?>> TAG_KEY_GENERIC_STREAM_CODEC = StreamCodec.of(
        (buf, tagKey) -> {
            buf.writeIdentifier(tagKey.registry().identifier());
            buf.writeIdentifier(tagKey.location());
        },
        buf -> {
            final Identifier registryLocation = buf.readIdentifier();
            final Identifier tagLocation = buf.readIdentifier();

            final ResourceKey<Registry<Object>> registryKey = ResourceKey.createRegistryKey(registryLocation);

            return TagKey.create(registryKey, tagLocation);
        }
    );

    public static final Codec<ResourceTag> RESOURCE_TAG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TAG_KEY_CODEC.fieldOf("key").forGetter(ResourceTag::key),
        ResourceCodecs.CODEC.listOf().fieldOf("resources").forGetter(ResourceTag::resources)
    ).apply(instance, ResourceTag::new));

    public static final Codec<Optional<ResourceTag>> OPTIONAL_RESOURCE_TAG_CODEC = RESOURCE_TAG_CODEC.optionalFieldOf("tag").codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceTag> RESOURCE_TAG_STREAM_CODEC = StreamCodec.composite(
        TAG_KEY_GENERIC_STREAM_CODEC, ResourceTag::key,
        ResourceCodecs.STREAM_CODEC.apply(ByteBufCodecs.list()), ResourceTag::resources,
        ResourceTag::new
    );

    private ModCodecs() {
    }
}
