package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;

import java.util.function.Consumer;

import net.minecraft.world.item.ItemStack;

public final class CreativeModeTabItems {
    private CreativeModeTabItems() {
    }

    public static void appendBlocks(final Consumer<ItemStack> consumer) {
        for (final CableTiers tier : CableTiers.values()) {
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredImporters(tier));
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredExporters(tier));
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredDestructors(tier));
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredConstructors(tier));
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredDiskInterfaces(tier));
            appendDefaultBlockColor(consumer, Blocks.INSTANCE.getTieredAutocrafters(tier));
        }
    }

    private static void appendDefaultBlockColor(final Consumer<ItemStack> consumer, final BlockColorMap<?, ?> map) {
        consumer.accept(new ItemStack(map.getDefault()));
    }

    public static void appendColoredVariants(final Consumer<ItemStack> consumer) {
        for (final CableTiers tier : CableTiers.values()) {
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredImporters(tier));
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredExporters(tier));
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredDestructors(tier));
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredConstructors(tier));
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredDiskInterfaces(tier));
            appendColoredBlocks(consumer, Blocks.INSTANCE.getTieredAutocrafters(tier));
        }
    }

    private static void appendColoredBlocks(final Consumer<ItemStack> consumer, final BlockColorMap<?, ?> map) {
        map.forEach((color, id, block) -> {
            if (!map.isDefaultColor(color)) {
                consumer.accept(new ItemStack(block.get()));
            }
        });
    }
}
