package com.ultramega.cabletiers.neoforge.datagen;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.constructordestructor.AbstractConstructorDestructorBlock;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.direction.DefaultDirectionType;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class BlockStateProviderImpl extends BlockStateProvider {
    protected static final String BLOCK_PREFIX = "block";

    private final ExistingFileHelper existingFileHelper;

    public BlockStateProviderImpl(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for (final CableTiers tier : CableTiers.values()) {
            registerCableLike(Blocks.INSTANCE.getTieredImporters(tier), tier.getLowercaseName() + "_importer");
            registerCableLike(Blocks.INSTANCE.getTieredExporters(tier), tier.getLowercaseName() + "_exporter");
            registerConstructorDestructor(Blocks.INSTANCE.getTieredDestructors(tier), tier.getLowercaseName() + "_destructor");
            registerConstructorDestructor(Blocks.INSTANCE.getTieredConstructors(tier), tier.getLowercaseName() + "_constructor");
            registerDiskInterfaces(tier, tier.getLowercaseName() + "_disk_interface");
            registerAutocrafters(tier, tier.getLowercaseName() + "_autocrafter");
        }
    }

    private void registerCableLike(final BlockColorMap<?, ?> blockMap, final String type) {
        final ModelFile model = modelFile(createCableTiersIdentifier("block/" + type));
        blockMap.forEach((color, id, block) -> {
            final MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());
            final var cablePart = builder.part();
            cablePart.modelFile(getCableModel(color)).addModel();
            for (final Direction direction : Direction.values()) {
                final var part = builder.part();
                addDirectionalRotation(direction, part);
                part.modelFile(model).addModel()
                    .condition(DefaultDirectionType.FACE_CLICKED.getProperty(), direction);
            }
        });
    }

    private void registerConstructorDestructor(final BlockColorMap<?, ?> blockMap, final String type) {
        final ModelFile activeModel = modelFile(createCableTiersIdentifier(BLOCK_PREFIX + "/" + type + "/active"));
        final ModelFile inactiveModel = modelFile(createCableTiersIdentifier(BLOCK_PREFIX + "/" + type + "/inactive"));
        blockMap.forEach((color, id, block) -> {
            final MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());
            final var cablePart = builder.part();
            cablePart.modelFile(getCableModel(color)).addModel();
            for (final Direction direction : Direction.values()) {
                final var part = builder.part();
                addDirectionalRotation(direction, part);
                part.modelFile(activeModel)
                    .addModel()
                    .condition(DefaultDirectionType.FACE_CLICKED.getProperty(), direction)
                    .condition(AbstractConstructorDestructorBlock.ACTIVE, true)
                    .end();
                part.modelFile(inactiveModel)
                    .addModel()
                    .condition(DefaultDirectionType.FACE_CLICKED.getProperty(), direction)
                    .condition(AbstractConstructorDestructorBlock.ACTIVE, false)
                    .end();
            }
        });
    }

    private void registerDiskInterfaces(final CableTiers tier, final String name) {
        Blocks.INSTANCE.getTieredDiskInterfaces(tier).forEach((color, id, block) -> {
            final var builder = getVariantBuilder(block.get());
            builder.addModels(
                builder.partialState(),
                ConfiguredModel.builder().modelFile(
                    modelFile(createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/" + color.getName()))
                ).build()
            );
        });
    }

    private void registerAutocrafters(final CableTiers tier, final String name) {
        final ModelFile inactive = modelFile(createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/inactive"));
        Blocks.INSTANCE.getTieredAutocrafters(tier).forEach((color, id, block) -> {
            final ModelFile active = modelFile(createCableTiersIdentifier(BLOCK_PREFIX + "/" + name + "/" + color.getName()));
            final var builder = getVariantBuilder(block.get());
            builder.forAllStates(blockState -> {
                final ConfiguredModel.Builder<?> model = ConfiguredModel.builder();
                if (Boolean.TRUE.equals(blockState.getValue(AbstractActiveColoredDirectionalBlock.ACTIVE))) {
                    model.modelFile(active);
                } else {
                    model.modelFile(inactive);
                }
                final Direction direction = blockState.getValue(DefaultDirectionType.FACE_CLICKED.getProperty());
                addAutocrafterRotation(model, direction);
                return model.build();
            });
        });
    }

    private static void addDirectionalRotation(final Direction direction,
                                               final ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> part) {
        switch (direction) {
            case UP -> part.rotationX(270);
            case SOUTH -> part.rotationX(180);
            case DOWN -> part.rotationX(90);
            case WEST -> part.rotationY(270);
            case EAST -> part.rotationY(90);
            default -> {
                // do nothing
            }
        }
    }

    private void addAutocrafterRotation(final ConfiguredModel.Builder<?> model, final Direction direction) {
        if (direction.getAxis().isHorizontal()) {
            model.rotationX(90);
        } else if (direction == Direction.DOWN) {
            model.rotationX(180);
        }
        model.rotationY(direction.getAxis().isVertical() ? 0 : (((int) direction.toYRot()) + 180) % 360);
    }

    private ModelFile getCableModel(final DyeColor color) {
        return modelFile(createIdentifier("block/cable/" + color.getName()));
    }

    private ModelFile modelFile(final ResourceLocation location) {
        return new ModelFile.ExistingModelFile(location, existingFileHelper);
    }
}
