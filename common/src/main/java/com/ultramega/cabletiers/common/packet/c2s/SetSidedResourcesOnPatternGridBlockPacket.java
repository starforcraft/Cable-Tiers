package com.ultramega.cabletiers.common.packet.c2s;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.mixin.InvokerPatternGridContainerMenu;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public record SetSidedResourcesOnPatternGridBlockPacket(List<Optional<SidedResourceAmount>> sidedResources) implements CustomPacketPayload {
    public static final Type<SetSidedResourcesOnPatternGridBlockPacket> PACKET_TYPE =
        new Type<>(createCableTiersIdentifier("set_sided_resources_on_pattern_grid_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSidedResourcesOnPatternGridBlockPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, SidedResourceAmount.OPTIONAL_STREAM_CODEC), SetSidedResourcesOnPatternGridBlockPacket::sidedResources,
        SetSidedResourcesOnPatternGridBlockPacket::new
    );

    public static void handle(final SetSidedResourcesOnPatternGridBlockPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof PatternGridContainerMenu gridContainerMenu) {
            final PatternGridBlockEntity gridBlockEntity = ((InvokerPatternGridContainerMenu) gridContainerMenu).cabletiers$getPatternGrid();
            if (gridBlockEntity == null) {
                return;
            }

            final BlockPos pos = gridBlockEntity.getBlockPos();
            if (ctx.getPlayer().level().getBlockEntity(pos) instanceof SidedInput sidedInputGridBlock) {
                sidedInputGridBlock.cabletiers$setSidedResources(packet.sidedResources());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
