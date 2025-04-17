package com.ultramega.cabletiers.neoforge.datagen;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorBlock;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorBlock;
import com.ultramega.cabletiers.common.exporter.TieredExporterBlock;
import com.ultramega.cabletiers.common.importer.TieredImporterBlock;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.utils.ContentIds;

import com.refinedmods.refinedstorage.common.content.ColorMap;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class ItemModelProviderImpl extends ItemModelProvider {
    private static final String CABLE_TEXTURE_KEY = "cable";

    public ItemModelProviderImpl(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (final CableTiers tier : CableTiers.values()) {
            registerTieredImporters(tier);
            registerTieredExporters(tier);
            registerTieredDestructors(tier);
            registerTieredConstructors(tier);
            registerTieredDiskInterfaces(tier);
            registerTieredAutocrafters(tier);
        }
    }

    private void registerTieredImporters(final CableTiers tier) {
        final ResourceLocation base = createCableTiersIdentifier("item/importer/" + tier.toString().toLowerCase() + "_base");
        final ColorMap<TieredImporterBlock> blocks = Blocks.INSTANCE.getTieredImporters(tier);
        blocks.forEach((color, id, block) -> addCableTexture(color, id, base));
    }

    private void registerTieredExporters(final CableTiers tier) {
        final ResourceLocation base = createCableTiersIdentifier("item/exporter/" + tier.toString().toLowerCase() + "_base");
        final ColorMap<TieredExporterBlock> blocks = Blocks.INSTANCE.getTieredExporters(tier);
        blocks.forEach((color, id, block) -> addCableTexture(color, id, base));
    }

    private void registerTieredDestructors(final CableTiers tier) {
        final ResourceLocation base = createCableTiersIdentifier("item/destructor/" + tier.toString().toLowerCase() + "_base");
        final ColorMap<TieredDestructorBlock> blocks = Blocks.INSTANCE.getTieredDestructors(tier);
        blocks.forEach((color, id, block) -> addCableTexture(color, id, base));
    }

    private void registerTieredConstructors(final CableTiers tier) {
        final ResourceLocation base = createCableTiersIdentifier("item/constructor/" + tier.toString().toLowerCase() + "_base");
        final ColorMap<TieredConstructorBlock> blocks = Blocks.INSTANCE.getTieredConstructors(tier);
        blocks.forEach((color, id, block) -> addCableTexture(color, id, base));
    }

    private void registerTieredDiskInterfaces(final CableTiers tier) {
        final var blocks = Blocks.INSTANCE.getTieredDiskInterfaces(tier);
        blocks.forEach((color, id, block) -> getBuilder(id.getPath()).customLoader(
            (blockModelBuilder, existingFileHelper) -> new TieredColoredCustomLoaderBuilder<>(
                ContentIds.getContentId(tier, CableType.DISK_INTERFACE),
                blockModelBuilder,
                existingFileHelper,
                color,
                tier
            ) {
            }).end());
    }

    private void registerTieredAutocrafters(final CableTiers tier) {
        final var blocks = Blocks.INSTANCE.getTieredAutocrafters(tier);
        blocks.forEach((color, id, block) -> withExistingParent(
            id.getPath(),
            createCableTiersIdentifier("block/" + tier.toString().toLowerCase() + "_autocrafter/" + color.getName())
        ));
    }

    private void addCableTexture(final DyeColor color,
                                 final ResourceLocation id,
                                 final ResourceLocation base) {
        singleTexture(
            id.getPath(),
            base,
            CABLE_TEXTURE_KEY,
            createIdentifier("block/cable/" + color.getName())
        );
    }
}
