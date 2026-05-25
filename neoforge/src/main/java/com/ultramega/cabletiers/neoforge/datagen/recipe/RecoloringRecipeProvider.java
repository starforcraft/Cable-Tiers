package com.ultramega.cabletiers.neoforge.datagen.recipe;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.support.RecoloringRecipe;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Recipe;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class RecoloringRecipeProvider extends RecipeProvider {
    public RecoloringRecipeProvider(final HolderLookup.Provider registries, final RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        for (final CableTiers tier : CableTiers.values()) {
            Blocks.INSTANCE.getTieredImporters(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_importer"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.IMPORTER), color, block.get(), this.registries), null));
            Blocks.INSTANCE.getTieredExporters(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_exporter"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.EXPORTER), color, block.get(), this.registries), null));
            Blocks.INSTANCE.getTieredDestructors(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_destructor"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.DESTRUCTOR), color, block.get(), this.registries), null));
            Blocks.INSTANCE.getTieredConstructors(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_constructor"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.CONSTRUCTOR), color, block.get(), this.registries), null));
            Blocks.INSTANCE.getTieredDiskInterfaces(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_disk_interface"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.DISK_INTERFACE), color, block.get(), this.registries), null));
            Blocks.INSTANCE.getTieredAutocrafters(tier).forEach((color, id, block) ->
                this.output.accept(this.recipeId(color, tier.getLowercaseName() + "_autocrafter"),
                    RecoloringRecipe.create(tier.getItemTag(CableType.AUTOCRAFTER), color, block.get(), this.registries), null));
        }
    }

    private ResourceKey<Recipe<?>> recipeId(final DyeColor color, final String suffix) {
        final Identifier recipeId = createCableTiersIdentifier("coloring/" + color.getName() + "_" + suffix);
        return ResourceKey.create(Registries.RECIPE, recipeId);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(final PackOutput packOutput, final CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(final HolderLookup.Provider registries,
                                                      final RecipeOutput output) {
            return new RecoloringRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Cable Tiers recoloring recipes";
        }
    }
}
