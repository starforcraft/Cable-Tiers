package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record RemoveSidedResourcesOnPatternGridMenuPacket(int index) implements CustomPacketPayload {
    public static final Type<RemoveSidedResourcesOnPatternGridMenuPacket> PACKET_TYPE =
        new Type<>(createCableTiersIdentifier("remove_sided_resources_on_pattern_grid_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveSidedResourcesOnPatternGridMenuPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, RemoveSidedResourcesOnPatternGridMenuPacket::index,
        RemoveSidedResourcesOnPatternGridMenuPacket::new
    );

    public static void handle(final RemoveSidedResourcesOnPatternGridMenuPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof SidedInput sidedInputGridMenu) {
            sidedInputGridMenu.cabletiers$removeSidedResources(packet.index());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
