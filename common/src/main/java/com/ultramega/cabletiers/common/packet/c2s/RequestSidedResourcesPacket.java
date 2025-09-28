package com.ultramega.cabletiers.common.packet.c2s;

import com.ultramega.cabletiers.common.mixin.MixinPatternGridContainerMenuInvoker;
import com.ultramega.cabletiers.common.packet.s2c.SetSidedResourcesOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record RequestSidedResourcesPacket() implements CustomPacketPayload {
    public static final Type<RequestSidedResourcesPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("request_sided_resources"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSidedResourcesPacket> STREAM_CODEC = StreamCodec.unit(new RequestSidedResourcesPacket());

    public static void handle(final RequestSidedResourcesPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer
            && ((MixinPatternGridContainerMenuInvoker) serverPlayer.containerMenu).cabletiers$getPatternGrid() instanceof SidedInput sidedInput) {
            Platform.INSTANCE.sendPacketToClient(serverPlayer, new SetSidedResourcesOnPatternGridMenuPacket(sidedInput.cabletiers$getSidedResources()));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
