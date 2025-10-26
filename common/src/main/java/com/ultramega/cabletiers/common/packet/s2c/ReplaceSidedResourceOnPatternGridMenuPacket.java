package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record ReplaceSidedResourceOnPatternGridMenuPacket(Optional<ResourceAmount> resource, int index) implements CustomPacketPayload {
    public static final Type<ReplaceSidedResourceOnPatternGridMenuPacket> PACKET_TYPE =
        new Type<>(createCableTiersIdentifier("replace_sided_resource_on_pattern_grid_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReplaceSidedResourceOnPatternGridMenuPacket> STREAM_CODEC = StreamCodec.composite(
        ResourceCodecs.AMOUNT_STREAM_CODEC.apply(ByteBufCodecs::optional), ReplaceSidedResourceOnPatternGridMenuPacket::resource,
        ByteBufCodecs.INT, ReplaceSidedResourceOnPatternGridMenuPacket::index,
        ReplaceSidedResourceOnPatternGridMenuPacket::new
    );

    public static void handle(final ReplaceSidedResourceOnPatternGridMenuPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof SidedInput sidedInputGridMenu) {
            sidedInputGridMenu.cabletiers$replaceSidedResource(packet.resource().orElse(null), packet.index());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
