package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.api.core.CoreValidations;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SidedResourceAmount(ResourceAmount resource, Optional<Direction> inputDirection) {
    public static final Codec<SidedResourceAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceCodecs.AMOUNT_CODEC.fieldOf("resource").forGetter(SidedResourceAmount::resource),
        Direction.CODEC.optionalFieldOf("inputDirection").forGetter(SidedResourceAmount::inputDirection)
    ).apply(instance, SidedResourceAmount::new));
    public static final Codec<Optional<SidedResourceAmount>> OPTIONAL_CODEC = CODEC.optionalFieldOf("resource")
        .codec();
    public static final Codec<List<Optional<SidedResourceAmount>>> OPTIONAL_LIST_CODEC = Codec.list(OPTIONAL_CODEC)
        .fieldOf("resources")
        .codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, SidedResourceAmount> STREAM_CODEC = StreamCodec.composite(
        ResourceCodecs.AMOUNT_STREAM_CODEC, SidedResourceAmount::resource,
        Direction.STREAM_CODEC.apply(ByteBufCodecs::optional), SidedResourceAmount::inputDirection,
        SidedResourceAmount::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<SidedResourceAmount>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);

    /**
     * @param resource the resource, must be non-null
     */
    public SidedResourceAmount {
        validate(resource);
    }

    public static void validate(final ResourceAmount resource) {
        CoreValidations.validateNotNull(resource, "Resource must not be null");
    }
}
