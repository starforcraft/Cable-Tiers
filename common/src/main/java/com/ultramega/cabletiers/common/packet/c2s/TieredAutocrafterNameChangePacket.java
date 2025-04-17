package com.ultramega.cabletiers.common.packet.c2s;

import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record TieredAutocrafterNameChangePacket(String name) implements CustomPacketPayload {
    public static final Type<TieredAutocrafterNameChangePacket> PACKET_TYPE = new Type<>(
        createCableTiersIdentifier("autocrafter_name_change")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TieredAutocrafterNameChangePacket> STREAM_CODEC = StreamCodec
        .composite(
            ByteBufCodecs.STRING_UTF8, TieredAutocrafterNameChangePacket::name,
            TieredAutocrafterNameChangePacket::new
        );

    public static void handle(final TieredAutocrafterNameChangePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof TieredAutocrafterContainerMenu containerMenu) {
            containerMenu.changeName(packet.name);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
