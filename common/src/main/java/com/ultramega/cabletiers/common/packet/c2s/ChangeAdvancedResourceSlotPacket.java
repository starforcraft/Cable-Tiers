package com.ultramega.cabletiers.common.packet.c2s;

import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceSlot;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record ChangeAdvancedResourceSlotPacket(boolean tryAlternatives, int index) implements CustomPacketPayload {
    public static final Type<ChangeAdvancedResourceSlotPacket> PACKET_TYPE = new Type<>(createCableTiersIdentifier("change_advanced_resource_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeAdvancedResourceSlotPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, ChangeAdvancedResourceSlotPacket::tryAlternatives,
        ByteBufCodecs.INT, ChangeAdvancedResourceSlotPacket::index,
        ChangeAdvancedResourceSlotPacket::new
    );

    public static void handle(final ChangeAdvancedResourceSlotPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof AbstractTieredFilterContainerMenu<?> containerMenu) {
            if (containerMenu.getSlot(packet.index()) instanceof AdvancedResourceSlot slot) {
                slot.trulyChange(ItemStack.EMPTY, packet.tryAlternatives());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
