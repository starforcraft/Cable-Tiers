package com.ultramega.cabletiers.fabric;

import com.ultramega.cabletiers.common.AbstractClientModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.packet.OpenAdvancedFilterPacket;
import com.ultramega.cabletiers.common.packet.UpdateAdvancedFilterPacket;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil;
import com.ultramega.cabletiers.fabric.storage.diskinterface.TieredDiskInterfaceBlockEntityRendererImpl;
import com.ultramega.cabletiers.fabric.storage.diskinterface.TieredDiskInterfaceUnbakedModel;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.support.packet.PacketHandler;
import com.refinedmods.refinedstorage.fabric.support.render.QuadRotators;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class ClientModInitializerImpl extends AbstractClientModInitializer implements ClientModInitializer {
    private static final String BLOCK_PREFIX = "block";
    private static final String ITEM_PREFIX = "item";

    @Override
    public void onInitializeClient() {
        setRenderLayers();
        registerPacketHandlers();
        registerBlockEntityRenderers();
        registerCustomModels();
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

    private void setRenderLayers() {
        for (final CableTiers tier : CableTiers.values()) {
            setCutout(Blocks.INSTANCE.getTieredImporters(tier));
            setCutout(Blocks.INSTANCE.getTieredExporters(tier));
            setCutout(Blocks.INSTANCE.getTieredDestructors(tier));
            setCutout(Blocks.INSTANCE.getTieredDiskInterfaces(tier));
        }
    }

    private void setCutout(final BlockColorMap<?, ?> blockMap) {
        blockMap.values().forEach(this::setCutout);
    }

    private void setCutout(final Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
    }

    private void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
            OpenAdvancedFilterPacket.PACKET_TYPE,
            wrapHandler(OpenAdvancedFilterPacket::handle)
        );
        ClientPlayNetworking.registerGlobalReceiver(
            UpdateAdvancedFilterPacket.PACKET_TYPE,
            wrapHandler(UpdateAdvancedFilterPacket::handle)
        );
    }

    private void registerBlockEntityRenderers() {
        for (final CableTiers tier : CableTiers.values()) {
            BlockEntityRenderers.register(
                BlockEntities.INSTANCE.getTieredDiskInterfaces(tier),
                ctx -> new TieredDiskInterfaceBlockEntityRendererImpl<>()
            );
        }
    }

    private void registerCustomModels() {
        final QuadRotators quadRotators = new QuadRotators();
        ModelLoadingPlugin.register(pluginContext -> {
            for (final CableTiers tier : CableTiers.values()) {
                registerCustomDiskInterfaceModels(pluginContext, quadRotators, tier);
            }
        });
    }

    private void registerCustomDiskInterfaceModels(final ModelLoadingPlugin.Context pluginContext,
                                                   final QuadRotators quadRotators,
                                                   final CableTiers tier) {
        pluginContext.resolveModel().register(context -> {
            if (context.id().getNamespace().equals(CableTiersIdentifierUtil.MOD_ID)
                && context.id().getPath().startsWith(ITEM_PREFIX + "/")
                && context.id().getPath().endsWith("disk_interface")) {
                final boolean isDefault = !context.id().getPath().endsWith("_disk_interface");
                final DyeColor color = isDefault
                    ? Blocks.INSTANCE.getTieredDiskInterfaces(tier).getDefault().getColor()
                    : DyeColor.byName(context.id().getPath().replace("_disk_interface", "")
                    .replace(ITEM_PREFIX + "/", ""), Blocks.INSTANCE.getTieredDiskInterfaces(tier).getDefault().getColor());
                return new TieredDiskInterfaceUnbakedModel(quadRotators, color);
            }
            if (context.id().getNamespace().equals(CableTiersIdentifierUtil.MOD_ID)
                && context.id().getPath().startsWith(BLOCK_PREFIX + "/disk_interface/")
                && !context.id().getPath().startsWith(BLOCK_PREFIX + "/disk_interface/base_")
                && !context.id().getPath().equals(BLOCK_PREFIX + "/disk_interface/inactive")) {
                final DyeColor color = DyeColor.byName(
                    context.id().getPath().replace(BLOCK_PREFIX + "/disk_interface/", ""),
                    Blocks.INSTANCE.getTieredDiskInterfaces(tier).getDefault().getColor()
                );
                return new TieredDiskInterfaceUnbakedModel(quadRotators, color);
            }
            return null;
        });
    }

    private static <T extends CustomPacketPayload> ClientPlayNetworking.PlayPayloadHandler<T> wrapHandler(final PacketHandler<T> handler) {
        return (packet, ctx) -> handler.handle(packet, ctx::player);
    }
}
