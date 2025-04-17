package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredConstructorBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredDestructorBlockEntity;
import com.ultramega.cabletiers.common.exporter.AbstractTieredExporterBlockEntity;
import com.ultramega.cabletiers.common.importer.AbstractTieredImporterBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;

public record BlockEntityProviders(
    BlockEntityProvider<AbstractTieredImporterBlockEntity> tieredImporter,
    BlockEntityProvider<AbstractTieredExporterBlockEntity> tieredExporter,
    BlockEntityProvider<AbstractTieredDestructorBlockEntity> tieredDestructor,
    BlockEntityProvider<AbstractTieredConstructorBlockEntity> tieredConstructor,
    BlockEntityProvider<AbstractTieredDiskInterfaceBlockEntity> tieredDiskInterface) {
}
