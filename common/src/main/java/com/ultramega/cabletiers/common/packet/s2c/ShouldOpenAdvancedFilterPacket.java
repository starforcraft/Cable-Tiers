package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.AbstractClientModInitializer;
import com.ultramega.cabletiers.common.packet.c2s.ChangeAdvancedResourceSlot;
import com.ultramega.cabletiers.common.utils.ModCodecs;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.Optional;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.TagKey;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record ShouldOpenAdvancedFilterPacket(int slotIndex,
                                             Optional<TagKey<?>> selectedTagKey,
                                             Optional<PlatformResourceKey> selectedResource,
                                             boolean tryAlternatives) implements CustomPacketPayload {
    public static final Type<ShouldOpenAdvancedFilterPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("should_open_advanced_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShouldOpenAdvancedFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ShouldOpenAdvancedFilterPacket::slotIndex,
        ModCodecs.TAG_KEY_GENERIC_STREAM_CODEC.apply(ByteBufCodecs::optional), ShouldOpenAdvancedFilterPacket::selectedTagKey,
        ResourceCodecs.STREAM_CODEC.apply(ByteBufCodecs::optional), ShouldOpenAdvancedFilterPacket::selectedResource,
        ByteBufCodecs.BOOL, ShouldOpenAdvancedFilterPacket::tryAlternatives,
        ShouldOpenAdvancedFilterPacket::new
    );

    public static void handle(final ShouldOpenAdvancedFilterPacket packet, final PacketContext ctx) {
        if (Screen.hasShiftDown()) {
            // Remove the filter instead
            Platform.INSTANCE.sendPacketToServer(new ChangeAdvancedResourceSlot(packet.tryAlternatives(), packet.slotIndex()));
        } else {
            AbstractClientModInitializer.openAdvancedFilterScreen(
                packet.slotIndex(),
                packet.selectedTagKey().orElse(null),
                packet.selectedResource().orElse(null));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
