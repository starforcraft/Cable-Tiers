package com.ultramega.cabletiers.neoforge.datagen.tag;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.Blocks;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;

public class ItemTagsProviderImpl extends ItemTagsProvider {
    public ItemTagsProviderImpl(final PackOutput packOutput,
                                final CompletableFuture<HolderLookup.Provider> registries,
                                final TagsProvider<Block> blockTagsProvider,
                                final ExistingFileHelper existingFileHelper) {
        super(packOutput, registries, blockTagsProvider.contentsGetter(), MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        for (final CableTiers tier : CableTiers.values()) {
            addAllToTag(tier.getItemTag(CableType.IMPORTER),
                Blocks.INSTANCE.getTieredImporters(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.EXPORTER),
                Blocks.INSTANCE.getTieredExporters(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.DESTRUCTOR),
                Blocks.INSTANCE.getTieredDestructors(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.CONSTRUCTOR),
                Blocks.INSTANCE.getTieredConstructors(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.DISK_INTERFACE),
                Blocks.INSTANCE.getTieredDiskInterfaces(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.AUTOCRAFTER),
                Blocks.INSTANCE.getTieredAutocrafters(tier).values().stream()
                    .map(block -> (Supplier<Item>) block::asItem)
                    .toList());
            addAllToTag(tier.getItemTag(CableType.INTERFACE),
                List.of(() -> Blocks.INSTANCE.getTieredInterfaces(tier).get().asItem()));
        }
    }

    private <T extends Item> void addAllToTag(final TagKey<Item> t, final Collection<Supplier<T>> items) {
        tag(t).add(items.stream().map(Supplier::get).toArray(Item[]::new)).replace(false);
    }
}
