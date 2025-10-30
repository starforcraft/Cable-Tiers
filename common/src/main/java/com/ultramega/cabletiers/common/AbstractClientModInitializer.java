package com.ultramega.cabletiers.common;

import com.ultramega.cabletiers.common.advancedfilter.AdvancedFilterScreen;
import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu;
import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterScreen;
import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorContainerMenu;
import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorScreen;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorContainerMenu;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorScreen;
import com.ultramega.cabletiers.common.exporter.TieredExporterContainerMenu;
import com.ultramega.cabletiers.common.exporter.TieredExporterScreen;
import com.ultramega.cabletiers.common.iface.TieredInterfaceContainerMenu;
import com.ultramega.cabletiers.common.iface.TieredInterfaceScreen;
import com.ultramega.cabletiers.common.importer.TieredImporterContainerMenu;
import com.ultramega.cabletiers.common.importer.TieredImporterScreen;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceContainerMenu;
import com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceScreen;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public abstract class AbstractClientModInitializer {
    protected static void registerScreens(final com.refinedmods.refinedstorage.common.AbstractClientModInitializer.ScreenRegistration registration) {
        for (final CableTiers tier : CableTiers.values()) {
            registration.<TieredImporterContainerMenu, TieredImporterScreen>register(Menus.INSTANCE.getTieredImporters(tier),
                (containerMenu, inventory, title) ->
                    new TieredImporterScreen(containerMenu, inventory, title, tier));
            registration.<TieredExporterContainerMenu, TieredExporterScreen>register(Menus.INSTANCE.getTieredExporters(tier),
                (containerMenu, inventory, title) ->
                    new TieredExporterScreen(containerMenu, inventory, title, tier));
            registration.<TieredDestructorContainerMenu, TieredDestructorScreen>register(Menus.INSTANCE.getTieredDestructors(tier),
                (containerMenu, inventory, title) ->
                    new TieredDestructorScreen(containerMenu, inventory, title, tier));
            registration.<TieredConstructorContainerMenu, TieredConstructorScreen>register(Menus.INSTANCE.getTieredConstructors(tier),
                (containerMenu, inventory, title) ->
                    new TieredConstructorScreen(containerMenu, inventory, title, tier));
            registration.<TieredDiskInterfaceContainerMenu, TieredDiskInterfaceScreen>register(Menus.INSTANCE.getTieredDiskInterfaces(tier),
                (containerMenu, inventory, title) ->
                    new TieredDiskInterfaceScreen(containerMenu, inventory, title, tier));
            registration.<TieredAutocrafterContainerMenu, TieredAutocrafterScreen>register(Menus.INSTANCE.getTieredAutocrafters(tier),
                (containerMenu, inventory, title) ->
                    new TieredAutocrafterScreen(containerMenu, inventory, title, tier));
            registration.<TieredInterfaceContainerMenu, TieredInterfaceScreen>register(Menus.INSTANCE.getTieredInterfaces(tier),
                (containerMenu, inventory, title) ->
                    new TieredInterfaceScreen(containerMenu, inventory, title, tier));
        }
    }

    public static void openAdvancedFilterScreen(final int slotIndex,
                                                @Nullable final TagKey<?> selectedTagKey,
                                                @Nullable final PlatformResourceKey selectedResource) {
        Minecraft.getInstance().setScreen(new AdvancedFilterScreen(
            Minecraft.getInstance().screen,
            Minecraft.getInstance().player.getInventory(),
            slotIndex,
            selectedTagKey,
            selectedResource,
            createCableTiersTranslation("gui", "advanced_filter.tag_filter")
        ));
    }
}
