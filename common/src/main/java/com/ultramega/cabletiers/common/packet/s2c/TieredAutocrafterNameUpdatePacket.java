package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record TieredAutocrafterNameUpdatePacket(Component name) implements CustomPacketPayload {
    public static final Type<TieredAutocrafterNameUpdatePacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("autocrafter_name_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TieredAutocrafterNameUpdatePacket> STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC, TieredAutocrafterNameUpdatePacket::name,
        TieredAutocrafterNameUpdatePacket::new
    );

    public static void handle(final TieredAutocrafterNameUpdatePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof TieredAutocrafterContainerMenu containerMenu) {
            containerMenu.nameChanged(packet.name);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
