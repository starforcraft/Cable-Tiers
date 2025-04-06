package com.ultramega.cabletiers.common;

import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorContainerMenu;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorContainerMenu;
import com.ultramega.cabletiers.common.exporters.TieredExporterContainerMenu;
import com.ultramega.cabletiers.common.importers.TieredImporterContainerMenu;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.registry.Items;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceContainerMenu;
import com.ultramega.cabletiers.common.utils.BlockEntityProviders;
import com.ultramega.cabletiers.common.utils.BlockEntityTypeFactory;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.constructordestructor.ConstructorData;
import com.refinedmods.refinedstorage.common.content.ExtendedMenuTypeFactory;
import com.refinedmods.refinedstorage.common.content.RegistryCallback;
import com.refinedmods.refinedstorage.common.exporter.ExporterData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AbstractModInitializer {
    protected void registerBlocks(final RegistryCallback<Block> callback,
                                  final BlockEntityProviders blockEntityProviders) {
        Blocks.INSTANCE.setTieredImporters(blockEntityProviders.tieredImporter())
            .values()
            .forEach(importer -> importer.registerBlocks(callback));
        Blocks.INSTANCE.setTieredExporters(blockEntityProviders.tieredExporter())
            .values()
            .forEach(exporter -> exporter.registerBlocks(callback));
        Blocks.INSTANCE.setTieredDestructors(blockEntityProviders.tieredDestructor())
            .values()
            .forEach(destructor -> destructor.registerBlocks(callback));
        Blocks.INSTANCE.setTieredConstructors(blockEntityProviders.tieredConstructor())
            .values()
            .forEach(constructor -> constructor.registerBlocks(callback));
        Blocks.INSTANCE.setTieredDiskInterfaces(blockEntityProviders.tieredDiskInterface())
            .values()
            .forEach(constructor -> constructor.registerBlocks(callback));
    }

    protected void registerItems(final RegistryCallback<Item> callback) {
        for (final CableTiers tier : CableTiers.values()) {
            Blocks.INSTANCE.getTieredImporters(tier).registerItems(callback, Items.INSTANCE::addTieredImporter);
            Blocks.INSTANCE.getTieredExporters(tier).registerItems(callback, Items.INSTANCE::addTieredExporter);
            Blocks.INSTANCE.getTieredDestructors(tier).registerItems(callback, Items.INSTANCE::addTieredDestructor);
            Blocks.INSTANCE.getTieredConstructors(tier).registerItems(callback, Items.INSTANCE::addTieredConstructor);
            Blocks.INSTANCE.getTieredDiskInterfaces(tier).registerItems(callback, Items.INSTANCE::addTieredDiskInterface);
        }
    }

    protected final void registerBlockEntities(final RegistryCallback<BlockEntityType<?>> callback,
                                               final BlockEntityTypeFactory typeFactory,
                                               final BlockEntityProviders providers) {
        for (final CableTiers tier : CableTiers.values()) {
            BlockEntities.INSTANCE.addTieredImporters(tier, callback.register(
                tier.getContentId(CableType.IMPORTER),
                () -> typeFactory.create(tier, providers.tieredImporter(), Blocks.INSTANCE.getTieredImporters(tier).toArray())
            ));
            BlockEntities.INSTANCE.addTieredExporters(tier, callback.register(
                tier.getContentId(CableType.EXPORTER),
                () -> typeFactory.create(tier, providers.tieredExporter(), Blocks.INSTANCE.getTieredExporters(tier).toArray())
            ));
            BlockEntities.INSTANCE.addTieredDestructors(tier, callback.register(
                tier.getContentId(CableType.DESTRUCTOR),
                () -> typeFactory.create(tier, providers.tieredDestructor(), Blocks.INSTANCE.getTieredDestructors(tier).toArray())
            ));
            BlockEntities.INSTANCE.addTieredConstructors(tier, callback.register(
                tier.getContentId(CableType.CONSTRUCTOR),
                () -> typeFactory.create(tier, providers.tieredConstructor(), Blocks.INSTANCE.getTieredConstructors(tier).toArray())
            ));
            BlockEntities.INSTANCE.addTieredDiskInterface(tier, callback.register(
                tier.getContentId(CableType.DISK_INTERFACE),
                () -> typeFactory.create(tier, providers.tieredDiskInterface(), Blocks.INSTANCE.getTieredDiskInterfaces(tier).toArray())
            ));
        }
    }

    protected final void registerMenus(final RegistryCallback<MenuType<?>> callback,
                                       final ExtendedMenuTypeFactory extendedMenuTypeFactory) {
        for (final CableTiers tier : CableTiers.values()) {
            Menus.INSTANCE.setTieredImporters(tier, callback.register(
                tier.getContentId(CableType.IMPORTER),
                () -> extendedMenuTypeFactory.create((syncId, inventory, containerData) ->
                    new TieredImporterContainerMenu(syncId, inventory, containerData, tier), ResourceContainerData.STREAM_CODEC)
            ));
            Menus.INSTANCE.setTieredExporters(tier, callback.register(
                tier.getContentId(CableType.EXPORTER),
                () -> extendedMenuTypeFactory.create((syncId, inventory, containerData) ->
                    new TieredExporterContainerMenu(syncId, inventory, containerData, tier), ExporterData.STREAM_CODEC)
            ));
            Menus.INSTANCE.setTieredDestructors(tier, callback.register(
                tier.getContentId(CableType.DESTRUCTOR),
                () -> extendedMenuTypeFactory.create((syncId, inventory, containerData) ->
                    new TieredDestructorContainerMenu(syncId, inventory, containerData, tier), ResourceContainerData.STREAM_CODEC)
            ));
            Menus.INSTANCE.setTieredConstructors(tier, callback.register(
                tier.getContentId(CableType.CONSTRUCTOR),
                () -> extendedMenuTypeFactory.create((syncId, inventory, containerData) ->
                    new TieredConstructorContainerMenu(syncId, inventory, containerData, tier), ConstructorData.STREAM_CODEC)
            ));
            Menus.INSTANCE.setTieredDiskInterfaces(tier, callback.register(
                tier.getContentId(CableType.DISK_INTERFACE),
                () -> extendedMenuTypeFactory.create((syncId, inventory, containerData) ->
                    new TieredDiskInterfaceContainerMenu(syncId, inventory, containerData, tier), ResourceContainerData.STREAM_CODEC)
            ));
        }
    }

    protected final void registerUpgradeMappings() {
        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.IMPORTER_NO_STACK)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getSpeedUpgrade(), 4)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getRegulatorUpgrade(), 4);
        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.IMPORTER_NO_STACK_SPEED)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getRegulatorUpgrade(), 4);

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.EXPORTER_NO_STACK)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getSpeedUpgrade(), 4)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getRegulatorUpgrade(), 4)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getAutocraftingUpgrade());
        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.EXPORTER_NO_STACK_SPEED)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getRegulatorUpgrade(), 4)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getAutocraftingUpgrade());

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.DESTRUCTOR_NO_SPEED)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getFortune1Upgrade())
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getFortune2Upgrade())
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getFortune3Upgrade())
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getSilkTouchUpgrade());

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.CONSTRUCTOR_NO_STACK)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getSpeedUpgrade(), 4)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getAutocraftingUpgrade());
        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.CONSTRUCTOR_NO_STACK_SPEED)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getAutocraftingUpgrade());

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(TieredUpgradeDestinations.DISK_INTERFACE_NO_STACK)
            .add(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getSpeedUpgrade());
    }
}
