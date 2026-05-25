package com.ultramega.cabletiers.neoforge.datagen.model;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.misc.ProcessorItem;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;

public class MainRecipeProvider extends RecipeProvider {
    public MainRecipeProvider(final HolderLookup.Provider registries, final RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        this.tieredAutocrafters();
        this.tieredConstructors();
        this.tieredDestructors();
        this.tieredDiskInterfaces();
        this.tieredExporters();
        this.tieredImporters();
        this.tieredInterfaces();
    }

    private void tieredAutocrafters() {
        this.elite(
            Blocks.INSTANCE.getTieredAutocrafters(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getAutocrafter().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredAutocrafters(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredAutocrafters(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredAutocrafters(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredAutocrafters(CableTiers.ULTRA).getDefault());
    }

    private void tieredConstructors() {
        this.elite(
            Blocks.INSTANCE.getTieredConstructors(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getConstructor().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredConstructors(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredConstructors(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredConstructors(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredConstructors(CableTiers.ULTRA).getDefault());
    }

    private void tieredDestructors() {
        this.elite(
            Blocks.INSTANCE.getTieredDestructors(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getDestructor().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredDestructors(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredDestructors(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredDestructors(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredDestructors(CableTiers.ULTRA).getDefault());
    }

    private void tieredDiskInterfaces() {
        this.elite(
            Blocks.INSTANCE.getTieredDiskInterfaces(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getDiskInterface().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredDiskInterfaces(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredDiskInterfaces(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredDiskInterfaces(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredDiskInterfaces(CableTiers.ULTRA).getDefault());
    }

    private void tieredExporters() {
        this.elite(
            Blocks.INSTANCE.getTieredExporters(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getExporter().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredExporters(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredExporters(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredExporters(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredExporters(CableTiers.ULTRA).getDefault());
    }

    private void tieredImporters() {
        this.elite(
            Blocks.INSTANCE.getTieredImporters(CableTiers.ELITE).getDefault(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getImporter().getDefault());
        this.ultra(
            Blocks.INSTANCE.getTieredImporters(CableTiers.ULTRA).getDefault(),
            Blocks.INSTANCE.getTieredImporters(CableTiers.ELITE).getDefault());
        this.mega(
            Blocks.INSTANCE.getTieredImporters(CableTiers.MEGA).getDefault(),
            Blocks.INSTANCE.getTieredImporters(CableTiers.ULTRA).getDefault());
    }

    private void tieredInterfaces() {
        this.elite(
            Blocks.INSTANCE.getTieredInterfaces(CableTiers.ELITE).get(),
            com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getInterface());
        this.ultra(
            Blocks.INSTANCE.getTieredInterfaces(CableTiers.ULTRA).get(),
            Blocks.INSTANCE.getTieredInterfaces(CableTiers.ELITE).get());
        this.mega(
            Blocks.INSTANCE.getTieredInterfaces(CableTiers.MEGA).get(),
            Blocks.INSTANCE.getTieredInterfaces(CableTiers.ULTRA).get());
    }

    private void elite(final ItemLike result, final ItemLike middle) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, result)
            .pattern("IMI")
            .pattern("MAM")
            .pattern("IMI")
            .define('A', Items.INSTANCE.getProcessor(ProcessorItem.Type.ADVANCED))
            .define('I', net.minecraft.world.item.Items.IRON_BLOCK)
            .define('M', middle)
            .unlockedBy("has_machine", this.has(middle))
            .save(this.output);
    }

    private void ultra(final ItemLike result, final ItemLike middle) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, result)
            .pattern("DMD")
            .pattern("MAM")
            .pattern("DMD")
            .define('A', Items.INSTANCE.getProcessor(ProcessorItem.Type.ADVANCED))
            .define('D', net.minecraft.world.item.Items.DIAMOND_BLOCK)
            .define('M', middle)
            .unlockedBy("has_elite_machine", this.has(middle))
            .save(this.output);
    }

    private void mega(final ItemLike result, final ItemLike middle) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, result)
            .pattern("IMI")
            .pattern("MAM")
            .pattern("IMI")
            .define('A', net.minecraft.world.item.Items.DRAGON_HEAD)
            .define('I', net.minecraft.world.item.Items.NETHERITE_BLOCK)
            .define('M', middle)
            .unlockedBy("has_ultra_machine", this.has(middle))
            .save(this.output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(final PackOutput packOutput, final CompletableFuture<Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(final HolderLookup.Provider registries,
                                                      final RecipeOutput output) {
            return new MainRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Cable Tiers recipes";
        }
    }
}

