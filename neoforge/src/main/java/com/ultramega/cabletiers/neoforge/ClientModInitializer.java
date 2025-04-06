package com.ultramega.cabletiers.neoforge;

import com.ultramega.cabletiers.common.AbstractClientModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.utils.ContentIds;
import com.ultramega.cabletiers.neoforge.storage.diskinterface.TieredDiskInterfaceBlockEntityRendererImpl;
import com.ultramega.cabletiers.neoforge.storage.diskinterface.TieredDiskInterfaceGeometryLoader;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ClientModInitializer extends AbstractClientModInitializer {
    private ClientModInitializer() {
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent e) {
        registerBlockEntityRenderer();
    }

    @SubscribeEvent
    public static void onRegisterCustomModels(final ModelEvent.RegisterGeometryLoaders e) {
        for (final CableTiers tier : CableTiers.values()) {
            e.register(ContentIds.getContentId(tier, CableType.DISK_INTERFACE), new TieredDiskInterfaceGeometryLoader());
        }
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(final RegisterMenuScreensEvent e) {
        registerScreens(new com.refinedmods.refinedstorage.common.AbstractClientModInitializer.ScreenRegistration() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                final MenuType<? extends M> type,
                final com.refinedmods.refinedstorage.common.AbstractClientModInitializer.ScreenConstructor<M, U> factory
            ) {
                e.register(type, factory::create);
            }
        });
    }

    private static void registerBlockEntityRenderer() {
        for (final CableTiers tier : CableTiers.values()) {
            BlockEntityRenderers.register(
                BlockEntities.INSTANCE.getTieredDiskInterfaces(tier),
                ctx -> new TieredDiskInterfaceBlockEntityRendererImpl<>()
            );
        }
    }
}
