package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.utils.ClearableSidedResource;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record ClearSidedResourceOnPatternGridMenuPacket() implements CustomPacketPayload {
    public static final Type<ClearSidedResourceOnPatternGridMenuPacket> PACKET_TYPE =
        new Type<>(createCableTiersIdentifier("clear_sided_resource_on_pattern_grid_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearSidedResourceOnPatternGridMenuPacket> STREAM_CODEC =
        StreamCodec.unit(new ClearSidedResourceOnPatternGridMenuPacket());

    public static void handle(final ClearSidedResourceOnPatternGridMenuPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof ClearableSidedResource sidedInputGridMenu) {
            sidedInputGridMenu.cabletiers$clearSidedResources();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
