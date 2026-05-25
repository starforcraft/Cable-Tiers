package com.ultramega.cabletiers.fabric;

import com.ultramega.cabletiers.common.AbstractClientModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.packet.s2c.ClearSidedResourceOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.ReplaceSidedResourceOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.SetSidedResourcesOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.ShouldOpenAdvancedFilterPacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterLockedUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterNameUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.UpdateAdvancedFilterPacket;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.fabric.storage.diskinterface.FabricTieredDiskInterfaceBlockEntityRenderer;
import com.ultramega.cabletiers.fabric.storage.diskinterface.TieredDiskInterfaceItemModelUnbaked;
import com.ultramega.cabletiers.fabric.storage.diskinterface.TieredDiskInterfaceUnbakedBlockStateModel;

import com.refinedmods.refinedstorage.common.support.packet.PacketHandler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class ClientModInitializerImpl extends AbstractClientModInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        this.registerPacketHandlers();
        this.registerBlockEntityRenderers();
        this.registerCustomModels();
        registerScreens(new com.refinedmods.refinedstorage.common.AbstractClientModInitializer.ScreenRegistration() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                final MenuType<? extends M> type,
                final com.refinedmods.refinedstorage.common.AbstractClientModInitializer.ScreenConstructor<M, U> factory
            ) {
                MenuScreens.register(type, factory::create);
            }
        });
    }

    private void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ShouldOpenAdvancedFilterPacket.PACKET_TYPE, wrapHandler(ShouldOpenAdvancedFilterPacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(UpdateAdvancedFilterPacket.PACKET_TYPE, wrapHandler(UpdateAdvancedFilterPacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(TieredAutocrafterLockedUpdatePacket.PACKET_TYPE, wrapHandler(TieredAutocrafterLockedUpdatePacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(TieredAutocrafterNameUpdatePacket.PACKET_TYPE, wrapHandler(TieredAutocrafterNameUpdatePacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(SetSidedResourcesOnPatternGridMenuPacket.PACKET_TYPE, wrapHandler(SetSidedResourcesOnPatternGridMenuPacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(ReplaceSidedResourceOnPatternGridMenuPacket.PACKET_TYPE,
            wrapHandler(ReplaceSidedResourceOnPatternGridMenuPacket::handle));
        ClientPlayNetworking.registerGlobalReceiver(ClearSidedResourceOnPatternGridMenuPacket.PACKET_TYPE,
            wrapHandler(ClearSidedResourceOnPatternGridMenuPacket::handle));
    }

    private void registerBlockEntityRenderers() {
        for (final CableTiers tier : CableTiers.values()) {
            BlockEntityRenderers.register(
                BlockEntities.INSTANCE.getTieredDiskInterfaces(tier),
                ctx -> new FabricTieredDiskInterfaceBlockEntityRenderer<>()
            );
        }
    }

    private void registerCustomModels() {
        CustomUnbakedBlockStateModel.register(createCableTiersIdentifier("tiered_disk_interface"), TieredDiskInterfaceUnbakedBlockStateModel.MODEL_CODEC);
        ItemModels.ID_MAPPER.put(createCableTiersIdentifier("tiered_disk_interface"), TieredDiskInterfaceItemModelUnbaked.CODEC);
    }

    private static <T extends CustomPacketPayload> ClientPlayNetworking.PlayPayloadHandler<T> wrapHandler(final PacketHandler<T> handler) {
        return (packet, ctx) -> handler.handle(packet, ctx::player);
    }
}
