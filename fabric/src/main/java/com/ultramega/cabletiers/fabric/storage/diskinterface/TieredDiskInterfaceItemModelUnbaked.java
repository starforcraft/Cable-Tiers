package com.ultramega.cabletiers.fabric.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.fabric.storage.diskinterface.DiskInterfaceItemModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModel.BakingContext;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.joml.Matrix4fc;

public class TieredDiskInterfaceItemModelUnbaked implements ItemModel.Unbaked {
    public static final MapCodec<TieredDiskInterfaceItemModelUnbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        CableTiers.CODEC.fieldOf("tier").forGetter(unbaked -> unbaked.tier),
        DyeColor.CODEC.fieldOf("color").forGetter(unbaked -> unbaked.color)
    ).apply(instance, TieredDiskInterfaceItemModelUnbaked::new));

    private final CableTiers tier;
    private final DyeColor color;

    public TieredDiskInterfaceItemModelUnbaked(final CableTiers tier, final DyeColor color) {
        this.tier = tier;
        this.color = color;
    }

    @Override
    public MapCodec<? extends ItemModel.Unbaked> type() {
        return CODEC;
    }

    @Override
    public ItemModel bake(final BakingContext bakingContext, final Matrix4fc matrix4fc) {
        final ModelBaker baker = bakingContext.blockModelBaker();
        final ResolvedModel baseModel = baker.getModel(TieredDiskInterfaceRenderingProperties.getActiveBaseModel(this.tier, this.color));
        final List<BakedQuad> baseQuads = baseModel
            .bakeTopGeometry(baseModel.getTopTextureSlots(), baker, BlockModelRotation.IDENTITY).getAll();
        final List<BakedQuad> ledQuads = baker.getModel(TieredDiskInterfaceRenderingProperties.INACTIVE_LED_MODEL)
            .bakeTopGeometry(baseModel.getTopTextureSlots(), baker, BlockModelRotation.IDENTITY).getAll();
        final Map<Item, List<BakedQuad>> diskQuads = RefinedStorageClientApi.INSTANCE.getDiskModelsByItem()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    final ResolvedModel diskModel = baker.getModel(entry.getValue());
                    return diskModel.bakeTopGeometry(diskModel.getTopTextureSlots(), baker,
                        BlockModelRotation.IDENTITY).getAll();
                }));
        return new DiskInterfaceItemModel(baseQuads, diskQuads, ledQuads,
            ModelRenderProperties.fromResolvedModel(baker, baseModel, baseModel.getTopTextureSlots()));
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(TieredDiskInterfaceRenderingProperties.getActiveBaseModel(this.tier, this.color));
        resolver.markDependency(TieredDiskInterfaceRenderingProperties.INACTIVE_LED_MODEL);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(resolver::markDependency);
    }
}
