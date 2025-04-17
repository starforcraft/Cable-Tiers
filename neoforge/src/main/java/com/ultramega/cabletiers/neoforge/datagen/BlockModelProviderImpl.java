package com.ultramega.cabletiers.neoforge.datagen;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.utils.ContentIds;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.neoforge.datagen.BlockStateProviderImpl.BLOCK_PREFIX;

public class BlockModelProviderImpl extends BlockModelProvider {
    private static final String PARTICLE_TEXTURE = "particle";
    private static final String CUTOUT_TEXTURE = "cutout";

    private static final ResourceLocation TIERED_AUTOCRAFTER = createCableTiersIdentifier("block/tiered_autocrafter");
    private static final ResourceLocation EMISSIVE_NORTH_CUTOUT = createIdentifier("block/emissive_north_cutout");

    private static final ResourceLocation NORTH_CUTOUT = createIdentifier("block/north_cutout");

    private static final String CUTOUT_NORTH_COLOR = "cutout_north_color";
    private static final String CUTOUT_EAST_COLOR = "cutout_east_color";
    private static final String CUTOUT_SOUTH_COLOR = "cutout_south_color";
    private static final String CUTOUT_WEST_COLOR = "cutout_west_color";
    private static final String CUTOUT_UP_COLOR = "cutout_up_color";
    private static final String CUTOUT_NORTH_TIER = "cutout_north_tier";
    private static final String CUTOUT_EAST_TIER = "cutout_east_tier";
    private static final String CUTOUT_SOUTH_TIER = "cutout_south_tier";
    private static final String CUTOUT_WEST_TIER = "cutout_west_tier";
    private static final String CUTOUT_UP_TIER = "cutout_up_tier";
    private static final String NORTH = "north";
    private static final String EAST = "east";
    private static final String SOUTH = "south";
    private static final String WEST = "west";
    private static final String UP = "up";
    private static final String DOWN = "down";

    public BlockModelProviderImpl(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (final CableTiers tier : CableTiers.values()) {
            registerTieredDiskInterfaces(tier);
            registerTieredAutocrafters(tier);
        }
    }

    private void registerTieredDiskInterfaces(final CableTiers tier) {
        registerRightLeftBackFrontTopModel(Blocks.INSTANCE.getTieredDiskInterfaces(tier), "disk_interface", "base_", tier);
        Blocks.INSTANCE.getTieredDiskInterfaces(tier)
            .forEach((color, id, block) -> getBuilder(BLOCK_PREFIX + "/" + tier.toString().toLowerCase() + "_disk_interface/" + color.getName())
                .customLoader((blockModelBuilder, existingFileHelper) -> new TieredColoredCustomLoaderBuilder<>(
                    ContentIds.getContentId(tier, CableType.DISK_INTERFACE),
                    blockModelBuilder,
                    existingFileHelper,
                    color,
                    tier
                ) {
                }).end());
    }

    private void registerTieredAutocrafters(final CableTiers tier) {
        final ResourceLocation side = createCableTiersIdentifier("block/autocrafter/side");
        final ResourceLocation top = createCableTiersIdentifier("block/autocrafter/top");
        final ResourceLocation bottom = createCableTiersIdentifier("block/autocrafter/bottom");
        Blocks.INSTANCE.getTieredAutocrafters(tier).forEach((color, id, autocrafter) -> {
            final ResourceLocation cutoutSideColor = createCableTiersIdentifier("block/autocrafter/cutouts/side_color/" + color.getName());
            final ResourceLocation cutoutSideTier = createCableTiersIdentifier("block/autocrafter/cutouts/side_tier/" + tier.toString().toLowerCase());
            final ResourceLocation cutoutTopColor = createCableTiersIdentifier("block/autocrafter/cutouts/top_color/" + color.getName());
            final ResourceLocation cutoutTopTier = createCableTiersIdentifier("block/autocrafter/cutouts/top_tier/" + tier.toString().toLowerCase());
            withExistingParent("block/" + tier.toString().toLowerCase() + "_autocrafter/" + color.getName(), TIERED_AUTOCRAFTER)
                .texture(PARTICLE_TEXTURE, side)
                .texture(NORTH, side)
                .texture(EAST, side)
                .texture(SOUTH, side)
                .texture(WEST, side)
                .texture(UP, top)
                .texture(DOWN, bottom)
                .texture(CUTOUT_NORTH_COLOR, cutoutSideColor)
                .texture(CUTOUT_EAST_COLOR, cutoutSideColor)
                .texture(CUTOUT_SOUTH_COLOR, cutoutSideColor)
                .texture(CUTOUT_WEST_COLOR, cutoutSideColor)
                .texture(CUTOUT_UP_COLOR, cutoutTopColor)
                .texture(CUTOUT_NORTH_TIER, cutoutSideTier)
                .texture(CUTOUT_EAST_TIER, cutoutSideTier)
                .texture(CUTOUT_SOUTH_TIER, cutoutSideTier)
                .texture(CUTOUT_WEST_TIER, cutoutSideTier)
                .texture(CUTOUT_UP_TIER, cutoutTopTier);
        });
        final ResourceLocation cutoutSideColor = createCableTiersIdentifier("block/autocrafter/cutouts/side_color/inactive");
        final ResourceLocation cutoutSideTier = createCableTiersIdentifier("block/autocrafter/cutouts/side_tier/" + tier.toString().toLowerCase());
        final ResourceLocation cutoutTopColor = createCableTiersIdentifier("block/autocrafter/cutouts/top_color/inactive");
        final ResourceLocation cutoutTopTier = createCableTiersIdentifier("block/autocrafter/cutouts/top_tier/" + tier.toString().toLowerCase());
        withExistingParent("block/" + tier.toString().toLowerCase() + "_autocrafter/inactive", TIERED_AUTOCRAFTER)
            .texture(PARTICLE_TEXTURE, side)
            .texture(NORTH, side)
            .texture(EAST, side)
            .texture(SOUTH, side)
            .texture(WEST, side)
            .texture(UP, top)
            .texture(DOWN, bottom)
            .texture(CUTOUT_NORTH_COLOR, cutoutSideColor)
            .texture(CUTOUT_EAST_COLOR, cutoutSideColor)
            .texture(CUTOUT_SOUTH_COLOR, cutoutSideColor)
            .texture(CUTOUT_WEST_COLOR, cutoutSideColor)
            .texture(CUTOUT_UP_COLOR, cutoutTopColor)
            .texture(CUTOUT_NORTH_TIER, cutoutSideTier)
            .texture(CUTOUT_EAST_TIER, cutoutSideTier)
            .texture(CUTOUT_SOUTH_TIER, cutoutSideTier)
            .texture(CUTOUT_WEST_TIER, cutoutSideTier)
            .texture(CUTOUT_UP_TIER, cutoutTopTier);
    }

    private void registerRightLeftBackFrontTopModel(final BlockColorMap<?, ?> blockMap,
                                                    final String name,
                                                    final String modelPrefix,
                                                    final CableTiers tier) {
        blockMap.forEach((color, id, block) -> {
            final ResourceLocation cutout = createIdentifier(BLOCK_PREFIX + "/" + name + "/cutouts/" + color.getName());
            registerRightLeftBackFrontTopModel(name, modelPrefix + color.getName(), cutout, EMISSIVE_NORTH_CUTOUT, false, tier);
        });
        final ResourceLocation inactiveCutout = createIdentifier(BLOCK_PREFIX + "/" + name + "/cutouts/inactive");
        registerRightLeftBackFrontTopModel(name, "inactive", inactiveCutout, NORTH_CUTOUT, true, tier);
    }

    private void registerRightLeftBackFrontTopModel(final String name,
                                                    final String variantName,
                                                    final ResourceLocation cutout,
                                                    final ResourceLocation baseModel,
                                                    final boolean inactive,
                                                    final CableTiers tier) {
        final ResourceLocation side = createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/side");
        final ResourceLocation front = createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/" + tier.toString().toLowerCase() + "_front"
            + (inactive ? "_inactive" : ""));
        final ResourceLocation top = createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/top");
        final ResourceLocation bottom = createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/bottom");
        withExistingParent(BLOCK_PREFIX + "/" + tier.toString().toLowerCase() + "_" + name + "/" + variantName, baseModel)
            .texture(PARTICLE_TEXTURE, side)
            .texture(NORTH, front)
            .texture(EAST, side)
            .texture(SOUTH, side)
            .texture(WEST, side)
            .texture(UP, top)
            .texture(DOWN, bottom)
            .texture(CUTOUT_TEXTURE, cutout);
    }
}
