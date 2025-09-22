package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SidedInputPatternState(List<Optional<SidedResourceAmount>> sidedResources) {
    public static final Codec<SidedInputPatternState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(SidedResourceAmount.OPTIONAL_CODEC).fieldOf("sidedResources").forGetter(SidedInputPatternState::sidedResources)
    ).apply(instance, SidedInputPatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SidedInputPatternState> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, SidedResourceAmount.OPTIONAL_STREAM_CODEC), SidedInputPatternState::sidedResources,
        SidedInputPatternState::new
    );
}
