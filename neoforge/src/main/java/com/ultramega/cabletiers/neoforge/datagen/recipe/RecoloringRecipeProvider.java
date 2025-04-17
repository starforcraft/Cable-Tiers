package com.ultramega.cabletiers.neoforge.datagen.recipe;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.Blocks;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class RecoloringRecipeProvider extends RecipeProvider {
    public RecoloringRecipeProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(final RecipeOutput output) {
        for (final CableTiers tier : CableTiers.values()) {
            Blocks.INSTANCE.getTieredImporters(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.IMPORTER), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_importer")));
            Blocks.INSTANCE.getTieredExporters(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.EXPORTER), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_exporter")));
            Blocks.INSTANCE.getTieredDestructors(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.DESTRUCTOR), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_destructor")));
            Blocks.INSTANCE.getTieredConstructors(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.CONSTRUCTOR), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_constructor")));
            Blocks.INSTANCE.getTieredDiskInterfaces(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.DISK_INTERFACE), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_disk_interface")));
            Blocks.INSTANCE.getTieredAutocrafters(tier).forEach((color, id, block) ->
                recipe(tier.getItemTag(CableType.AUTOCRAFTER), block.get().asItem(), color)
                    .save(output, recipeId(color, tier.toString().toLowerCase() + "_autocrafter")));
        }
    }

    private ResourceLocation recipeId(final DyeColor color, final String suffix) {
        return createCableTiersIdentifier("coloring/" + color.getName() + "_" + suffix);
    }

    private ShapelessRecipeBuilder recipe(final TagKey<Item> dyeable, final Item result, final DyeColor color) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
            .requires(dyeable)
            .requires(getDyeTag(color))
            .unlockedBy("has_" + dyeable.location().getPath(), has(dyeable));
    }

    private static TagKey<Item> getDyeTag(final DyeColor color) {
        return switch (color) {
            case RED -> Tags.Items.DYES_RED;
            case WHITE -> Tags.Items.DYES_WHITE;
            case ORANGE -> Tags.Items.DYES_ORANGE;
            case MAGENTA -> Tags.Items.DYES_MAGENTA;
            case LIGHT_BLUE -> Tags.Items.DYES_LIGHT_BLUE;
            case YELLOW -> Tags.Items.DYES_YELLOW;
            case LIME -> Tags.Items.DYES_LIME;
            case PINK -> Tags.Items.DYES_PINK;
            case GRAY -> Tags.Items.DYES_GRAY;
            case LIGHT_GRAY -> Tags.Items.DYES_LIGHT_GRAY;
            case CYAN -> Tags.Items.DYES_CYAN;
            case PURPLE -> Tags.Items.DYES_PURPLE;
            case BLUE -> Tags.Items.DYES_BLUE;
            case BROWN -> Tags.Items.DYES_BROWN;
            case GREEN -> Tags.Items.DYES_GREEN;
            case BLACK -> Tags.Items.DYES_BLACK;
        };
    }
}
