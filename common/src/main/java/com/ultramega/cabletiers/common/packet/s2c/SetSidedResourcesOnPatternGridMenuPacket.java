package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record SetSidedResourcesOnPatternGridMenuPacket(List<Optional<SidedResourceAmount>> sidedResources) implements CustomPacketPayload {
    public static final Type<SetSidedResourcesOnPatternGridMenuPacket> PACKET_TYPE =
        new Type<>(createCableTiersIdentifier("set_sided_resources_on_pattern_grid_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSidedResourcesOnPatternGridMenuPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, SidedResourceAmount.OPTIONAL_STREAM_CODEC), SetSidedResourcesOnPatternGridMenuPacket::sidedResources,
        SetSidedResourcesOnPatternGridMenuPacket::new
    );

    public static void handle(final SetSidedResourcesOnPatternGridMenuPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof SidedInput sidedInputGridMenu) {
            sidedInputGridMenu.cabletiers$setSidedResources(packet.sidedResources());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
