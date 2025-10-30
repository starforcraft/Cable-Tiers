package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredConstructorBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredDestructorBlockEntity;
import com.ultramega.cabletiers.common.exporter.AbstractTieredExporterBlockEntity;
import com.ultramega.cabletiers.common.iface.TieredInterfaceBlockEntity;
import com.ultramega.cabletiers.common.importer.AbstractTieredImporterBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.entity.BlockEntityType;

import static java.util.Objects.requireNonNull;

public final class BlockEntities {
    public static final BlockEntities INSTANCE = new BlockEntities();

    private final Map<CableTiers, Supplier<BlockEntityType<AbstractTieredImporterBlockEntity>>> tieredImporters = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<AbstractTieredExporterBlockEntity>>> tieredExporters = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<AbstractTieredDestructorBlockEntity>>> tieredDestructors = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<AbstractTieredConstructorBlockEntity>>> tieredConstructors = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<AbstractTieredDiskInterfaceBlockEntity>>> tieredDiskInterface = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<TieredAutocrafterBlockEntity>>> tieredAutocrafter = new HashMap<>();
    private final Map<CableTiers, Supplier<BlockEntityType<TieredInterfaceBlockEntity>>> tieredInterface = new HashMap<>();

    public void addTieredImporter(final CableTiers tier,
                                  final Supplier<BlockEntityType<AbstractTieredImporterBlockEntity>> supplier) {
        this.tieredImporters.put(tier, supplier);
    }

    public BlockEntityType<AbstractTieredImporterBlockEntity> getTieredImporters(final CableTiers tier) {
        return requireNonNull(tieredImporters.get(tier)).get();
    }

    public void addTieredExporter(final CableTiers tier,
                                  final Supplier<BlockEntityType<AbstractTieredExporterBlockEntity>> supplier) {
        this.tieredExporters.put(tier, supplier);
    }

    public BlockEntityType<AbstractTieredExporterBlockEntity> getTieredExporters(final CableTiers tier) {
        return requireNonNull(tieredExporters.get(tier)).get();
    }

    public void addTieredDestructor(final CableTiers tier,
                                    final Supplier<BlockEntityType<AbstractTieredDestructorBlockEntity>> supplier) {
        this.tieredDestructors.put(tier, supplier);
    }

    public BlockEntityType<AbstractTieredDestructorBlockEntity> getTieredDestructors(final CableTiers tier) {
        return requireNonNull(tieredDestructors.get(tier)).get();
    }

    public void addTieredConstructor(final CableTiers tier,
                                     final Supplier<BlockEntityType<AbstractTieredConstructorBlockEntity>> supplier) {
        this.tieredConstructors.put(tier, supplier);
    }

    public BlockEntityType<AbstractTieredConstructorBlockEntity> getTieredConstructors(final CableTiers tier) {
        return requireNonNull(tieredConstructors.get(tier)).get();
    }

    public void addTieredDiskInterface(final CableTiers tier,
                                       final Supplier<BlockEntityType<AbstractTieredDiskInterfaceBlockEntity>> supplier) {
        this.tieredDiskInterface.put(tier, supplier);
    }

    public BlockEntityType<AbstractTieredDiskInterfaceBlockEntity> getTieredDiskInterfaces(final CableTiers tier) {
        return requireNonNull(tieredDiskInterface.get(tier)).get();
    }

    public void addTieredAutocrafter(final CableTiers tier,
                                     final Supplier<BlockEntityType<TieredAutocrafterBlockEntity>> supplier) {
        this.tieredAutocrafter.put(tier, supplier);
    }

    public BlockEntityType<TieredAutocrafterBlockEntity> getTieredAutocrafters(final CableTiers tier) {
        return requireNonNull(tieredAutocrafter.get(tier)).get();
    }

    public void addTieredInterface(final CableTiers tier,
                                   final Supplier<BlockEntityType<TieredInterfaceBlockEntity>> supplier) {
        this.tieredInterface.put(tier, supplier);
    }

    public BlockEntityType<TieredInterfaceBlockEntity> getTieredInterfaces(final CableTiers tier) {
        return requireNonNull(tieredInterface.get(tier)).get();
    }
}
