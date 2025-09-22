package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredConstructorBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredDestructorBlockEntity;
import com.ultramega.cabletiers.common.exporter.AbstractTieredExporterBlockEntity;
import com.ultramega.cabletiers.common.importer.AbstractTieredImporterBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;

public record BlockEntityProviders(
    BlockEntityTierProvider<AbstractTieredImporterBlockEntity> tieredImporter,
    BlockEntityTierProvider<AbstractTieredExporterBlockEntity> tieredExporter,
    BlockEntityTierProvider<AbstractTieredDestructorBlockEntity> tieredDestructor,
    BlockEntityTierProvider<AbstractTieredConstructorBlockEntity> tieredConstructor,
    BlockEntityTierProvider<AbstractTieredDiskInterfaceBlockEntity> tieredDiskInterface) {
}
