package com.ultramega.cabletiers.neoforge.datagen;

import com.ultramega.cabletiers.neoforge.datagen.loot.LootTableProviderImpl;
import com.ultramega.cabletiers.neoforge.datagen.recipe.RecoloringRecipeProvider;
import com.ultramega.cabletiers.neoforge.datagen.tag.BlockTagsProvider;
import com.ultramega.cabletiers.neoforge.datagen.tag.ItemTagsProviderImpl;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent e) {
        registerBlockModelProviders(e.getGenerator(), e.getExistingFileHelper());
        registerItemModelProviders(e.getGenerator(), e.getExistingFileHelper());
        registerBlockStateProviders(e.getGenerator(), e.getExistingFileHelper());
        registerLootTableProviders(e.getGenerator(), e.getLookupProvider());
        registerRecipeProviders(e.getGenerator(), e.getLookupProvider());
        registerTagProviders(e.getGenerator(), e.getLookupProvider(), e.getExistingFileHelper());
    }

    private static void registerBlockModelProviders(final DataGenerator generator,
                                                    final ExistingFileHelper existingFileHelper) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        mainPack.addProvider(output -> new BlockModelProviderImpl(output, existingFileHelper));
    }

    private static void registerBlockStateProviders(final DataGenerator generator,
                                                    final ExistingFileHelper existingFileHelper) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        mainPack.addProvider(output -> new BlockStateProviderImpl(output, existingFileHelper));
    }

    private static void registerItemModelProviders(final DataGenerator generator,
                                                   final ExistingFileHelper existingFileHelper) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        mainPack.addProvider(output -> new ItemModelProviderImpl(output, existingFileHelper));
    }

    private static void registerLootTableProviders(final DataGenerator generator,
                                                   final CompletableFuture<HolderLookup.Provider> provider) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        mainPack.addProvider(output -> new LootTableProviderImpl(output, provider));
    }

    private static void registerRecipeProviders(final DataGenerator generator,
                                                final CompletableFuture<HolderLookup.Provider> provider) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        mainPack.addProvider(output -> new RecoloringRecipeProvider(output, provider));
    }

    private static void registerTagProviders(final DataGenerator generator,
                                             final CompletableFuture<HolderLookup.Provider> lookupProvider,
                                             final ExistingFileHelper existingFileHelper) {
        final DataGenerator.PackGenerator mainPack = generator.getVanillaPack(true);
        final BlockTagsProvider blockTagsProvider = mainPack.addProvider(
            output -> new BlockTagsProvider(output, lookupProvider, existingFileHelper)
        );
        mainPack.addProvider(output -> new ItemTagsProviderImpl(
            output,
            lookupProvider,
            blockTagsProvider,
            existingFileHelper
        ));
    }
}
