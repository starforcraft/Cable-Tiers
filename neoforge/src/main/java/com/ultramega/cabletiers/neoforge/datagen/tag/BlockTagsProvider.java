package com.ultramega.cabletiers.neoforge.datagen.tag;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
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
            addAllToTag(tier.getBlockTag(CableType.IMPORTER), Blocks.INSTANCE.getTieredImporters(tier));
            addAllToTag(tier.getBlockTag(CableType.EXPORTER), Blocks.INSTANCE.getTieredExporters(tier));
            addAllToTag(tier.getBlockTag(CableType.DESTRUCTOR), Blocks.INSTANCE.getTieredDestructors(tier));
            addAllToTag(tier.getBlockTag(CableType.CONSTRUCTOR), Blocks.INSTANCE.getTieredConstructors(tier));
            addAllToTag(tier.getBlockTag(CableType.DISK_INTERFACE), Blocks.INSTANCE.getTieredDiskInterfaces(tier));
            addAllToTag(tier.getBlockTag(CableType.AUTOCRAFTER), Blocks.INSTANCE.getTieredAutocrafters(tier));
            addToTag(tier.getBlockTag(CableType.INTERFACE), Blocks.INSTANCE.getTieredInterfaces(tier).get());
        }

        for (final CableType type : CableType.values()) {
            for (final CableTiers tier : CableTiers.values()) {
                tag(MINEABLE).addTag(tier.getBlockTag(type));
            }
        }
    }

    private <T extends Block> void addAllToTag(final TagKey<Block> t, final BlockColorMap<?, ?> blocks) {
        for (final Block block : blocks.toArray()) {
            addToTag(t, block);
        }
    }

    private <T extends Block> void addToTag(final TagKey<Block> t, final Block block) {
        tag(t).add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow()).replace(false);
    }
}
