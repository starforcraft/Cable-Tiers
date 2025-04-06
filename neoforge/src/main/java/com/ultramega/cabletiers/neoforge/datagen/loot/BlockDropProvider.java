package com.ultramega.cabletiers.neoforge.datagen.loot;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;

public class BlockDropProvider extends BlockLootSubProvider {
    public BlockDropProvider(final HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        for (final CableTiers tier : CableTiers.values()) {
            Blocks.INSTANCE.getTieredImporters(tier).forEach((color, id, block) -> drop(block.get()));
            Blocks.INSTANCE.getTieredExporters(tier).forEach((color, id, block) -> drop(block.get()));
            Blocks.INSTANCE.getTieredDestructors(tier).forEach((color, id, block) -> drop(block.get()));
            Blocks.INSTANCE.getTieredConstructors(tier).forEach((color, id, block) -> drop(block.get()));
            Blocks.INSTANCE.getTieredDiskInterfaces(tier).forEach((color, id, block) -> drop(block.get()));
        }
    }

    private void drop(final Block block) {
        add(block, createSingleItemTable(block)
            .apply(copyName()));
    }

    private static CopyComponentsFunction.Builder copyName() {
        return CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
            .include(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        final List<Block> blocks = new ArrayList<>();

        for (final CableTiers tier : CableTiers.values()) {
            blocks.addAll(Blocks.INSTANCE.getTieredImporters(tier).values());
            blocks.addAll(Blocks.INSTANCE.getTieredExporters(tier).values());
            blocks.addAll(Blocks.INSTANCE.getTieredDestructors(tier).values());
            blocks.addAll(Blocks.INSTANCE.getTieredConstructors(tier).values());
            blocks.addAll(Blocks.INSTANCE.getTieredDiskInterfaces(tier).values());
        }

        return blocks;
    }
}
