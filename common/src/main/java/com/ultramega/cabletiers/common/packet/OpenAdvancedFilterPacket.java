package com.ultramega.cabletiers.common.packet;

import com.ultramega.cabletiers.common.AbstractClientModInitializer;
import com.ultramega.cabletiers.common.utils.ModCodecs;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.Optional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.TagKey;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record OpenAdvancedFilterPacket(int slotIndex,
                                       Optional<TagKey<?>> selectedTagKey,
                                       Optional<PlatformResourceKey> selectedResource) implements CustomPacketPayload {
    public static final Type<OpenAdvancedFilterPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("open_advanced_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAdvancedFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, OpenAdvancedFilterPacket::slotIndex,
        ModCodecs.TAG_KEY_GENERIC_STREAM_CODEC.apply(ByteBufCodecs::optional), OpenAdvancedFilterPacket::selectedTagKey,
        ResourceCodecs.STREAM_CODEC.apply(ByteBufCodecs::optional), OpenAdvancedFilterPacket::selectedResource,
        OpenAdvancedFilterPacket::new
    );

    public static void handle(final OpenAdvancedFilterPacket packet, final PacketContext ctx) {
        AbstractClientModInitializer.openAdvancedFilterScreen(
            packet.slotIndex(),
            packet.selectedTagKey().orElse(null),
            packet.selectedResource().orElse(null));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
