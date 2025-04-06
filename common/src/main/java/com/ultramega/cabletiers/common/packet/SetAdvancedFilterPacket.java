package com.ultramega.cabletiers.common.packet;

import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;
import com.ultramega.cabletiers.common.utils.ModCodecs;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record SetAdvancedFilterPacket(int slotIndex, Optional<ResourceTag> resourceTag) implements CustomPacketPayload {
    public static final Type<SetAdvancedFilterPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("set_advanced_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAdvancedFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, SetAdvancedFilterPacket::slotIndex,
        ModCodecs.RESOURCE_TAG_STREAM_CODEC.apply(ByteBufCodecs::optional), SetAdvancedFilterPacket::resourceTag,
        SetAdvancedFilterPacket::new
    );

    public static void handle(final SetAdvancedFilterPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof AbstractTieredFilterContainerMenu<?> containerMenu) {
            containerMenu.setTagFilter(packet.slotIndex(), packet.resourceTag().orElse(null));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
