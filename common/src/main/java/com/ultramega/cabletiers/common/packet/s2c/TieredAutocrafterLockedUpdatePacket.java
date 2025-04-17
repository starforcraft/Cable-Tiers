package com.ultramega.cabletiers.common.packet.s2c;

import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record TieredAutocrafterLockedUpdatePacket(boolean locked) implements CustomPacketPayload {
    public static final Type<TieredAutocrafterLockedUpdatePacket> PACKET_TYPE = new Type<>(
        createCableTiersIdentifier("autocrafter_locked_update")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TieredAutocrafterLockedUpdatePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL, TieredAutocrafterLockedUpdatePacket::locked,
            TieredAutocrafterLockedUpdatePacket::new
        );

    public static void handle(final TieredAutocrafterLockedUpdatePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof TieredAutocrafterContainerMenu containerMenu) {
            containerMenu.lockedChanged(packet.locked);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
