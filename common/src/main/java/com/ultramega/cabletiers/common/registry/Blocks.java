package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredConstructorBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredDestructorBlockEntity;
import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorBlock;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorBlock;
import com.ultramega.cabletiers.common.exporters.AbstractTieredExporterBlockEntity;
import com.ultramega.cabletiers.common.exporters.TieredExporterBlock;
import com.ultramega.cabletiers.common.importers.AbstractTieredImporterBlockEntity;
import com.ultramega.cabletiers.common.importers.TieredImporterBlock;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceBlock;
import com.ultramega.cabletiers.common.utils.BlockEntityProvider;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;

import java.util.HashMap;
import java.util.Map;

import static com.refinedmods.refinedstorage.common.content.Blocks.CABLE_LIKE_COLOR;
import static java.util.Objects.requireNonNull;

public final class Blocks {
    public static final Blocks INSTANCE = new Blocks();

    private final Map<CableTiers, BlockColorMap<TieredImporterBlock, BaseBlockItem>> tieredImporters = new HashMap<>();
    private final Map<CableTiers, BlockColorMap<TieredExporterBlock, BaseBlockItem>> tieredExporters = new HashMap<>();
    private final Map<CableTiers, BlockColorMap<TieredDestructorBlock, BaseBlockItem>> tieredDestructors = new HashMap<>();
    private final Map<CableTiers, BlockColorMap<TieredConstructorBlock, BaseBlockItem>> tieredConstructors = new HashMap<>();
    private final Map<CableTiers, BlockColorMap<TieredDiskInterfaceBlock, BaseBlockItem>> tieredDiskInterface = new HashMap<>();

    private Blocks() {
    }

    public Map<CableTiers, BlockColorMap<TieredImporterBlock, BaseBlockItem>> setTieredImporters(
        final BlockEntityProvider<AbstractTieredImporterBlockEntity> provider) {
        for (final CableTiers tier : CableTiers.values()) {
            tieredImporters.put(tier, new BlockColorMap<>(
                (pos, state) -> new TieredImporterBlock(pos, state, tier, provider),
                tier.getContentId(CableType.IMPORTER),
                tier.getContentName(CableType.IMPORTER),
                CABLE_LIKE_COLOR
            ));
        }
        return tieredImporters;
    }

    public BlockColorMap<TieredImporterBlock, BaseBlockItem> getTieredImporters(final CableTiers tier) {
        return requireNonNull(tieredImporters.get(tier));
    }

    public Map<CableTiers, BlockColorMap<TieredExporterBlock, BaseBlockItem>> setTieredExporters(
        final BlockEntityProvider<AbstractTieredExporterBlockEntity> provider) {
        for (final CableTiers tier : CableTiers.values()) {
            tieredExporters.put(tier, new BlockColorMap<>(
                (pos, state) -> new TieredExporterBlock(pos, state, tier, provider),
                tier.getContentId(CableType.EXPORTER),
                tier.getContentName(CableType.EXPORTER),
                CABLE_LIKE_COLOR
            ));
        }
        return tieredExporters;
    }

    public BlockColorMap<TieredExporterBlock, BaseBlockItem> getTieredExporters(final CableTiers tier) {
        return requireNonNull(tieredExporters.get(tier));
    }

    public Map<CableTiers, BlockColorMap<TieredDestructorBlock, BaseBlockItem>> setTieredDestructors(
        final BlockEntityProvider<AbstractTieredDestructorBlockEntity> provider) {
        for (final CableTiers tier : CableTiers.values()) {
            tieredDestructors.put(tier, new BlockColorMap<>(
                (pos, state) -> new TieredDestructorBlock(pos, state, tier, provider),
                tier.getContentId(CableType.DESTRUCTOR),
                tier.getContentName(CableType.DESTRUCTOR),
                CABLE_LIKE_COLOR
            ));
        }
        return tieredDestructors;
    }

    public BlockColorMap<TieredDestructorBlock, BaseBlockItem> getTieredDestructors(final CableTiers tier) {
        return requireNonNull(tieredDestructors.get(tier));
    }

    public Map<CableTiers, BlockColorMap<TieredConstructorBlock, BaseBlockItem>> setTieredConstructors(
        final BlockEntityProvider<AbstractTieredConstructorBlockEntity> provider) {
        for (final CableTiers tier : CableTiers.values()) {
            tieredConstructors.put(tier, new BlockColorMap<>(
                (pos, state) -> new TieredConstructorBlock(pos, state, tier, provider),
                tier.getContentId(CableType.CONSTRUCTOR),
                tier.getContentName(CableType.CONSTRUCTOR),
                CABLE_LIKE_COLOR
            ));
        }
        return tieredConstructors;
    }

    public BlockColorMap<TieredConstructorBlock, BaseBlockItem> getTieredConstructors(final CableTiers tier) {
        return requireNonNull(tieredConstructors.get(tier));
    }

    public Map<CableTiers, BlockColorMap<TieredDiskInterfaceBlock, BaseBlockItem>> setTieredDiskInterfaces(
        final BlockEntityProvider<AbstractTieredDiskInterfaceBlockEntity> provider) {
        for (final CableTiers tier : CableTiers.values()) {
            tieredDiskInterface.put(tier, new BlockColorMap<>(
                (pos, state) -> new TieredDiskInterfaceBlock(pos, state, tier, provider),
                tier.getContentId(CableType.DISK_INTERFACE),
                tier.getContentName(CableType.DISK_INTERFACE),
                CABLE_LIKE_COLOR
            ));
        }
        return tieredDiskInterface;
    }

    public BlockColorMap<TieredDiskInterfaceBlock, BaseBlockItem> getTieredDiskInterfaces(final CableTiers tier) {
        return requireNonNull(tieredDiskInterface.get(tier));
    }
}
