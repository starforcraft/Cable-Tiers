package com.ultramega.cabletiers.neoforge.datagen.tag;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;

public class BlockTagsProvider extends TagsProvider<Block> {
    public static final TagKey<Block> MINEABLE = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("mineable/pickaxe"));

    public BlockTagsProvider(final PackOutput packOutput,
                             final CompletableFuture<HolderLookup.Provider> providerCompletableFuture,
                             final @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, Registries.BLOCK, providerCompletableFuture, MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        for (final CableTiers tier : CableTiers.values()) {
            markAsMineable(Blocks.INSTANCE.getTieredImporters(tier));
            markAsMineable(Blocks.INSTANCE.getTieredExporters(tier));
            markAsMineable(Blocks.INSTANCE.getTieredDestructors(tier));
            markAsMineable(Blocks.INSTANCE.getTieredConstructors(tier));
            markAsMineable(Blocks.INSTANCE.getTieredDiskInterfaces(tier));
        }
    }

    private void markAsMineable(final BlockColorMap<?, ?> map) {
        tag(MINEABLE).addAll(map.values().stream().map(b -> ResourceKey.create(
            Registries.BLOCK,
            BuiltInRegistries.BLOCK.getKey(b)
        )).toList());
    }
}
