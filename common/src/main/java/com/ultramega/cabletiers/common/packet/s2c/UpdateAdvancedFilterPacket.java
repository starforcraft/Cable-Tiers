package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;
import com.ultramega.cabletiers.common.utils.ModCodecs;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record UpdateAdvancedFilterPacket(List<Optional<ResourceTag>> resourceTags) implements CustomPacketPayload {
    public static final Type<UpdateAdvancedFilterPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("update_advanced_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateAdvancedFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ModCodecs.RESOURCE_TAG_STREAM_CODEC.apply(ByteBufCodecs::optional).apply(ByteBufCodecs.list()), UpdateAdvancedFilterPacket::resourceTags,
        UpdateAdvancedFilterPacket::new
    );

    public static void handle(final UpdateAdvancedFilterPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof AbstractTieredFilterContainerMenu<?> containerMenu) {
            final List<ResourceTag> tags = packet.resourceTags().stream()
                .map(optional -> optional.orElse(null))
                .collect(Collectors.toList());

            containerMenu.setTagKeys(tags);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
