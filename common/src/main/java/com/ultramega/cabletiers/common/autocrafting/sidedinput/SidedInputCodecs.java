package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class SidedInputCodecs {
    public static final Codec<SidedResourceAmount> SIDED_RESOURCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceCodecs.AMOUNT_CODEC.fieldOf("resource").forGetter(SidedResourceAmount::resource),
        Direction.CODEC.optionalFieldOf("inputDirection").forGetter(SidedResourceAmount::inputDirection)
    ).apply(instance, SidedResourceAmount::new));
    public static final Codec<Optional<SidedResourceAmount>> SIDED_RESOURCE_OPTIONAL_CODEC = SIDED_RESOURCE_CODEC.optionalFieldOf("resource")
        .codec();
    public static final Codec<List<Optional<SidedResourceAmount>>> SIDED_RESOURCE_OPTIONAL_LIST_CODEC = Codec.list(SIDED_RESOURCE_OPTIONAL_CODEC)
        .fieldOf("resources")
        .codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, SidedResourceAmount> SIDED_RESOURCE_STREAM_CODEC = StreamCodec.composite(
        ResourceCodecs.AMOUNT_STREAM_CODEC, SidedResourceAmount::resource,
        Direction.STREAM_CODEC.apply(ByteBufCodecs::optional), SidedResourceAmount::inputDirection,
        SidedResourceAmount::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<SidedResourceAmount>> SIDED_RESOURCE_OPTIONAL_STREAM_CODEC =
        ByteBufCodecs.optional(SIDED_RESOURCE_STREAM_CODEC);

    public static final Codec<SidedInputPatternState> SIDED_INPUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(SidedInputCodecs.SIDED_RESOURCE_OPTIONAL_CODEC).fieldOf("sidedResources").forGetter(SidedInputPatternState::sidedResources)
    ).apply(instance, SidedInputPatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SidedInputPatternState> SIDED_INPUT_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, SidedInputCodecs.SIDED_RESOURCE_OPTIONAL_STREAM_CODEC), SidedInputPatternState::sidedResources,
        SidedInputPatternState::new
    );

    private SidedInputCodecs() {
    }
}
