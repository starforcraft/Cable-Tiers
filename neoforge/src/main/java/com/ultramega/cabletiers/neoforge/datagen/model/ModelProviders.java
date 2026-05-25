package com.ultramega.cabletiers.neoforge.datagen.model;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.iface.TieredInterfaceBlock;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.neoforge.storage.diskinterface.TieredDiskInterfaceUnbakedBlockStateModel;

import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.direction.DefaultDirectionType;
import com.refinedmods.refinedstorage.neoforge.networking.ActiveInactiveCablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.neoforge.networking.CablePartUnbakedBlockStateModel;

import java.util.stream.Stream;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModelProviders extends ModelProvider {
    private static final TextureSlot CUTOUT = TextureSlot.create("cutout");

    private static final TextureSlot NORTH_CUTOUT_COLOR = TextureSlot.create("cutout_north_color");
    private static final TextureSlot EAST_CUTOUT_COLOR = TextureSlot.create("cutout_east_color");
    private static final TextureSlot SOUTH_CUTOUT_COLOR = TextureSlot.create("cutout_south_color");
    private static final TextureSlot WEST_CUTOUT_COLOR = TextureSlot.create("cutout_west_color");
    private static final TextureSlot UP_CUTOUT_COLOR = TextureSlot.create("cutout_up_color");

    private static final TextureSlot NORTH_CUTOUT_TIER = TextureSlot.create("cutout_north_tier");
    private static final TextureSlot EAST_CUTOUT_TIER = TextureSlot.create("cutout_east_tier");
    private static final TextureSlot SOUTH_CUTOUT_TIER = TextureSlot.create("cutout_south_tier");
    private static final TextureSlot WEST_CUTOUT_TIER = TextureSlot.create("cutout_west_tier");
    private static final TextureSlot UP_CUTOUT_TIER = TextureSlot.create("cutout_up_tier");

    private static final TextureSlot CABLE = TextureSlot.create("cable");

    private static final TextureSlot FRONT = TextureSlot.create("front");
    private static final TextureSlot BORDER = TextureSlot.create("border");

    private static final ModelTemplate NORTH_CUTOUT_MODEL = ModelTemplates.create(
        "refinedstorage:north_cutout",
        TextureSlot.PARTICLE,
        TextureSlot.NORTH,
        TextureSlot.EAST,
        TextureSlot.SOUTH,
        TextureSlot.WEST,
        TextureSlot.UP,
        TextureSlot.DOWN,
        CUTOUT
    );
    private static final ModelTemplate EMISSIVE_NORTH_CUTOUT_MODEL = ModelTemplates.create(
        "refinedstorage:emissive_north_cutout",
        TextureSlot.PARTICLE,
        TextureSlot.NORTH,
        TextureSlot.EAST,
        TextureSlot.SOUTH,
        TextureSlot.WEST,
        TextureSlot.UP,
        TextureSlot.DOWN,
        CUTOUT
    );

    private static final ModelTemplate ACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL = ModelTemplates.create(
        MOD_ID + ":constructor_destructor/active",
        BORDER,
        FRONT,
        CUTOUT
    );
    private static final ModelTemplate INACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL = ModelTemplates.create(
        MOD_ID + ":constructor_destructor/inactive",
        BORDER,
        FRONT,
        CUTOUT
    );

    private static final ModelTemplate ACTIVE_AUTOCRAFTER_MODEL = ModelTemplates.create(
        MOD_ID + ":autocrafter/active",
        TextureSlot.PARTICLE,
        TextureSlot.NORTH,
        TextureSlot.EAST,
        TextureSlot.SOUTH,
        TextureSlot.WEST,
        TextureSlot.UP,
        TextureSlot.DOWN,
        NORTH_CUTOUT_COLOR,
        EAST_CUTOUT_COLOR,
        SOUTH_CUTOUT_COLOR,
        WEST_CUTOUT_COLOR,
        UP_CUTOUT_COLOR,
        NORTH_CUTOUT_TIER,
        EAST_CUTOUT_TIER,
        SOUTH_CUTOUT_TIER,
        WEST_CUTOUT_TIER,
        UP_CUTOUT_TIER
    );

    private static final ModelTemplate INACTIVE_AUTOCRAFTER_MODEL = ModelTemplates.create(
        MOD_ID + ":autocrafter/inactive",
        TextureSlot.PARTICLE,
        TextureSlot.NORTH,
        TextureSlot.EAST,
        TextureSlot.SOUTH,
        TextureSlot.WEST,
        TextureSlot.UP,
        TextureSlot.DOWN,
        NORTH_CUTOUT_COLOR,
        EAST_CUTOUT_COLOR,
        SOUTH_CUTOUT_COLOR,
        WEST_CUTOUT_COLOR,
        UP_CUTOUT_COLOR,
        NORTH_CUTOUT_TIER,
        EAST_CUTOUT_TIER,
        SOUTH_CUTOUT_TIER,
        WEST_CUTOUT_TIER,
        UP_CUTOUT_TIER
    );

    private static final ModelTemplate IMPORTER_MODEL = ModelTemplates.create(
        MOD_ID + ":importer/base",
        BORDER
    );
    private static final ModelTemplate EXPORTER_MODEL = ModelTemplates.create(
        MOD_ID + ":exporter/base",
        BORDER
    );

    public ModelProviders(final PackOutput output) {
        super(output, MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.of();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.of();
    }

    @Override
    protected void registerModels(final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        for (final CableTiers tier : CableTiers.values()) {
            this.registerTieredImporters(tier, blockModels, itemModels);
            this.registerTieredExporters(tier, blockModels, itemModels);
            this.registerTieredConstructors(tier, blockModels, itemModels);
            this.registerTieredDestructors(tier, blockModels, itemModels);
            this.registerTieredDiskInterfaces(tier, blockModels, itemModels);
            this.registerTieredAutocrafters(tier, blockModels, itemModels);
            this.registerTieredInterfaces(tier, itemModels, blockModels);
        }
    }

    private static ModelTemplate getItemModel(final CableType type) {
        return ModelTemplates.createItem(
            MOD_ID + ":" + type.getLowercaseName() + "/base",
            TextureSlot.PARTICLE,
            BORDER,
            CABLE
        );
    }

    private void registerTieredImporters(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier partBorder = createCableTiersIdentifier("block/cable/" + tier.getLowercaseName() + "_part_border");
        final Identifier blockModel = IMPORTER_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_importer"),
            new TextureMapping().put(BORDER, texture(partBorder)),
            blockModels.modelOutput
        );
        Blocks.INSTANCE.getTieredImporters(tier).forEach((color, id, importer) -> {
            final Identifier itemModel = getItemModel(CableType.IMPORTER).create(
                createCableTiersIdentifier("item/" + tier.getLowercaseName() + "_importer/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(partBorder))
                    .put(BORDER, texture(partBorder))
                    .put(CABLE, texture(createIdentifier("block/cable/" + color.getName()))),
                itemModels.modelOutput
            );
            itemModels.itemModelOutput.accept(importer.get().asItem(), ItemModelUtils.plainModel(itemModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(importer.get(),
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                    new CablePartUnbakedBlockStateModel(color, blockModel)))));
        });
    }

    private void registerTieredExporters(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier partBorder = createCableTiersIdentifier("block/cable/" + tier.getLowercaseName() + "_part_border");
        final Identifier blockModel = EXPORTER_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_exporter"),
            new TextureMapping().put(BORDER, texture(partBorder)),
            blockModels.modelOutput
        );
        Blocks.INSTANCE.getTieredExporters(tier).forEach((color, id, exporter) -> {
            final Identifier itemModel = getItemModel(CableType.EXPORTER).create(
                createCableTiersIdentifier("item/" + tier.getLowercaseName() + "_exporter/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(partBorder))
                    .put(BORDER, texture(partBorder))
                    .put(CABLE, texture(createIdentifier("block/cable/" + color.getName()))),
                itemModels.modelOutput
            );
            itemModels.itemModelOutput.accept(exporter.get().asItem(), ItemModelUtils.plainModel(itemModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(exporter.get(),
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                    new CablePartUnbakedBlockStateModel(color, blockModel)))));
        });
    }

    private void registerTieredConstructors(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier front = createIdentifier("block/constructor/front");
        final Identifier border = createCableTiersIdentifier("block/cable/" + tier.getLowercaseName() + "_constructor_destructor");
        final Identifier inactiveBlockModel = INACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_constructor/inactive"),
            new TextureMapping()
                .put(BORDER, texture(border))
                .put(FRONT, texture(front))
                .put(CUTOUT, texture(createIdentifier("block/constructor/cutouts/inactive"))),
            blockModels.modelOutput
        );
        final Identifier activeBlockModel = ACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_constructor/active"),
            new TextureMapping()
                .put(BORDER, texture(border))
                .put(FRONT, texture(front))
                .put(CUTOUT, texture(createIdentifier("block/constructor/cutouts/active"))),
            blockModels.modelOutput
        );

        Blocks.INSTANCE.getTieredConstructors(tier).forEach((color, id, constructor) -> {
            final Identifier itemModel = getItemModel(CableType.CONSTRUCTOR).create(
                createCableTiersIdentifier("item/" + tier.getLowercaseName() + "_constructor/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(border))
                    .put(BORDER, texture(border))
                    .put(CABLE, texture(createIdentifier("block/cable/" + color.getName()))),
                itemModels.modelOutput
            );
            itemModels.itemModelOutput.accept(constructor.get().asItem(), ItemModelUtils.plainModel(itemModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(constructor.get(),
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                    new ActiveInactiveCablePartUnbakedBlockStateModel(
                        color,
                        activeBlockModel,
                        inactiveBlockModel
                    )))));
        });
    }

    private void registerTieredDestructors(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier front = createIdentifier("block/destructor/front");
        final Identifier border = createCableTiersIdentifier("block/cable/" + tier.getLowercaseName() + "_constructor_destructor");
        final Identifier inactiveBlockModel = INACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_destructor/inactive"),
            new TextureMapping()
                .put(BORDER, texture(border))
                .put(FRONT, texture(front))
                .put(CUTOUT, texture(createIdentifier("block/destructor/cutouts/inactive"))),
            blockModels.modelOutput
        );
        final Identifier activeBlockModel = ACTIVE_CONSTRUCTOR_DESTRUCTOR_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_destructor/active"),
            new TextureMapping()
                .put(BORDER, texture(border))
                .put(FRONT, texture(front))
                .put(CUTOUT, texture(createIdentifier("block/destructor/cutouts/active"))),
            blockModels.modelOutput
        );

        Blocks.INSTANCE.getTieredDestructors(tier).forEach((color, id, destructor) -> {
            final Identifier itemModel = getItemModel(CableType.DESTRUCTOR).create(
                createCableTiersIdentifier("item/" + tier.getLowercaseName() + "_destructor/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(border))
                    .put(BORDER, texture(border))
                    .put(CABLE, texture(createIdentifier("block/cable/" + color.getName()))),
                itemModels.modelOutput
            );
            itemModels.itemModelOutput.accept(destructor.get().asItem(), ItemModelUtils.plainModel(itemModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(destructor.get(),
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                    new ActiveInactiveCablePartUnbakedBlockStateModel(
                        color,
                        activeBlockModel,
                        inactiveBlockModel
                    )))));
        });
    }

    private void registerTieredDiskInterfaces(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier activeFront = createCableTiersIdentifier("block/disk_interface/" + tier.getLowercaseName() + "_front");
        final Identifier inactiveFront = createCableTiersIdentifier("block/disk_interface/" + tier.getLowercaseName() + "_front_inactive");
        final Identifier side = createCableTiersIdentifier("block/disk_interface/side");
        final Identifier top = createCableTiersIdentifier("block/disk_interface/top");
        final Identifier bottom = createCableTiersIdentifier("block/disk_interface/bottom");

        NORTH_CUTOUT_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/inactive"),
            new TextureMapping()
                .put(TextureSlot.PARTICLE, texture(side))
                .put(TextureSlot.NORTH, texture(inactiveFront))
                .put(TextureSlot.EAST, texture(side))
                .put(TextureSlot.SOUTH, texture(side))
                .put(TextureSlot.WEST, texture(side))
                .put(TextureSlot.UP, texture(top))
                .put(TextureSlot.DOWN, texture(bottom))
                .put(CUTOUT, texture(createIdentifier("block/disk_interface/cutouts/inactive"))),
            blockModels.modelOutput
        );

        Blocks.INSTANCE.getTieredDiskInterfaces(tier).forEach((color, id, diskInterface) -> {
            final Identifier activeModel = EMISSIVE_NORTH_CUTOUT_MODEL.create(
                createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(side))
                    .put(TextureSlot.NORTH, texture(activeFront))
                    .put(TextureSlot.EAST, texture(side))
                    .put(TextureSlot.SOUTH, texture(side))
                    .put(TextureSlot.WEST, texture(side))
                    .put(TextureSlot.UP, texture(top))
                    .put(TextureSlot.DOWN, texture(bottom))
                    .put(CUTOUT, texture(createIdentifier("block/disk_interface/cutouts/" + color.getName()))),
                blockModels.modelOutput
            );

            itemModels.itemModelOutput.accept(diskInterface.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(diskInterface.get(),
                MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                    new TieredDiskInterfaceUnbakedBlockStateModel(tier, color)))));
        });
    }

    private void registerTieredAutocrafters(final CableTiers tier, final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        final Identifier side = createCableTiersIdentifier("block/autocrafter/side");
        final Identifier top = createCableTiersIdentifier("block/autocrafter/top");
        final Identifier cutoutSideColor = createCableTiersIdentifier("block/autocrafter/cutouts/side_color/inactive");
        final Identifier cutoutSideTier = createCableTiersIdentifier("block/autocrafter/cutouts/side_tier/" + tier.getLowercaseName());
        final Identifier cutoutTopColor = createCableTiersIdentifier("block/autocrafter/cutouts/top_color/inactive");
        final Identifier cutoutTopTier = createCableTiersIdentifier("block/autocrafter/cutouts/top_tier/" + tier.getLowercaseName());
        final Identifier bottom = createCableTiersIdentifier("block/autocrafter/bottom");

        final Identifier inactiveModel = INACTIVE_AUTOCRAFTER_MODEL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_autocrafter/inactive"),
            new TextureMapping()
                .put(TextureSlot.PARTICLE, texture(side))
                .put(TextureSlot.NORTH, texture(side))
                .put(TextureSlot.EAST, texture(side))
                .put(TextureSlot.SOUTH, texture(side))
                .put(TextureSlot.WEST, texture(side))
                .put(TextureSlot.UP, texture(top))
                .put(TextureSlot.DOWN, texture(bottom))
                .put(NORTH_CUTOUT_COLOR, texture(cutoutSideColor))
                .put(EAST_CUTOUT_COLOR, texture(cutoutSideColor))
                .put(SOUTH_CUTOUT_COLOR, texture(cutoutSideColor))
                .put(WEST_CUTOUT_COLOR, texture(cutoutSideColor))
                .put(UP_CUTOUT_COLOR, texture(cutoutTopColor))
                .put(NORTH_CUTOUT_TIER, texture(cutoutSideTier))
                .put(EAST_CUTOUT_TIER, texture(cutoutSideTier))
                .put(SOUTH_CUTOUT_TIER, texture(cutoutSideTier))
                .put(WEST_CUTOUT_TIER, texture(cutoutSideTier))
                .put(UP_CUTOUT_TIER, texture(cutoutTopTier)),
            blockModels.modelOutput
        );

        Blocks.INSTANCE.getTieredAutocrafters(tier).forEach((color, id, autocrafter) -> {
            final Identifier cutoutSideColorActive = createCableTiersIdentifier("block/autocrafter/cutouts/side_color/" + color.getName());
            final Identifier cutoutSideTierActive = createCableTiersIdentifier("block/autocrafter/cutouts/side_tier/" + tier.getLowercaseName());
            final Identifier cutoutTopColorActive = createCableTiersIdentifier("block/autocrafter/cutouts/top_color/" + color.getName());
            final Identifier cutoutTopTierActive = createCableTiersIdentifier("block/autocrafter/cutouts/top_tier/" + tier.getLowercaseName());
            final Identifier activeModel = ACTIVE_AUTOCRAFTER_MODEL.create(
                createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_autocrafter/" + color.getName()),
                new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture(side))
                    .put(TextureSlot.NORTH, texture(side))
                    .put(TextureSlot.EAST, texture(side))
                    .put(TextureSlot.SOUTH, texture(side))
                    .put(TextureSlot.WEST, texture(side))
                    .put(TextureSlot.UP, texture(top))
                    .put(TextureSlot.DOWN, texture(bottom))
                    .put(NORTH_CUTOUT_COLOR, texture(cutoutSideColorActive))
                    .put(EAST_CUTOUT_COLOR, texture(cutoutSideColorActive))
                    .put(SOUTH_CUTOUT_COLOR, texture(cutoutSideColorActive))
                    .put(WEST_CUTOUT_COLOR, texture(cutoutSideColorActive))
                    .put(UP_CUTOUT_COLOR, texture(cutoutTopColorActive))
                    .put(NORTH_CUTOUT_TIER, texture(cutoutSideTierActive))
                    .put(EAST_CUTOUT_TIER, texture(cutoutSideTierActive))
                    .put(SOUTH_CUTOUT_TIER, texture(cutoutSideTierActive))
                    .put(WEST_CUTOUT_TIER, texture(cutoutSideTierActive))
                    .put(UP_CUTOUT_TIER, texture(cutoutTopTierActive)),
                blockModels.modelOutput
            );

            itemModels.itemModelOutput.accept(autocrafter.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(autocrafter.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveModel))
                    .select(true, plainVariant(activeModel)))
                .with(PropertyDispatch.modify(DefaultDirectionType.FACE_CLICKED.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(this.getAutocrafterXRot(direction))
                        .withYRot(this.getAutocrafterYRot(direction)))));
        });
    }

    private void registerTieredInterfaces(final CableTiers tier, final ItemModelGenerators itemModels, final BlockModelGenerators blockModels) {
        final Identifier activeModel = ModelTemplates.CUBE_ALL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_interface/active"),
            TextureMapping.cube(texture(createCableTiersIdentifier("block/interface/" + tier.getLowercaseName() + "_active"))),
            blockModels.modelOutput
        );
        final Identifier inactiveModel = ModelTemplates.CUBE_ALL.create(
            createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_interface/inactive"),
            TextureMapping.cube(texture(createCableTiersIdentifier("block/interface/" + tier.getLowercaseName() + "_inactive"))),
            blockModels.modelOutput
        );
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getTieredInterfaces(tier).get())
            .with(PropertyDispatch.initial(TieredInterfaceBlock.ACTIVE)
                .select(true, plainVariant(activeModel))
                .select(false, plainVariant(inactiveModel))));
        itemModels.itemModelOutput.accept(Blocks.INSTANCE.getTieredInterfaces(tier).get().asItem(),
            ItemModelUtils.plainModel(inactiveModel));
    }

    private Quadrant getAutocrafterXRot(final Direction direction) {
        return switch (direction) {
            case DOWN -> Quadrant.R180;
            case UP -> Quadrant.R0;
            case NORTH, SOUTH, WEST, EAST -> Quadrant.R90;
        };
    }

    private Quadrant getAutocrafterYRot(final Direction direction) {
        return switch (direction) {
            case DOWN, UP, NORTH -> Quadrant.R0;
            case SOUTH -> Quadrant.R180;
            case EAST -> Quadrant.R90;
            case WEST -> Quadrant.R270;
        };
    }

    private static Material texture(final Identifier location) {
        return new Material(location);
    }
}
