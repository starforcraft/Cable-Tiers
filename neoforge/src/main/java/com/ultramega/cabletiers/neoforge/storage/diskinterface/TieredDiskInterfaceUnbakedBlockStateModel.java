package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class TieredDiskInterfaceUnbakedBlockStateModel implements CustomUnbakedBlockStateModel {
    public static final MapCodec<TieredDiskInterfaceUnbakedBlockStateModel> MODEL_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            CableTiers.CODEC.fieldOf("tier").forGetter(unbaked -> unbaked.tier),
            DyeColor.CODEC.fieldOf("color").forGetter(unbaked -> unbaked.color)
        ).apply(instance, TieredDiskInterfaceUnbakedBlockStateModel::new));

    private final CableTiers tier;
    private final DyeColor color;

    public TieredDiskInterfaceUnbakedBlockStateModel(final CableTiers tier, final DyeColor color) {
        this.tier = tier;
        this.color = color;
    }

    @Override
    public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return MODEL_CODEC;
    }

    @Override
    public BlockStateModel bake(final ModelBaker modelBaker) {
        final ModelDebugName debugName = this.getClass()::toString;
        final Material.Baked particleMaterial = modelBaker.materials().get(
            new Material(createCableTiersIdentifier("block/disk_interface/top")),
            debugName
        );
        return new TieredDiskInterfaceBlockStateModel(particleMaterial, modelBaker, this.tier, this.color);
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(TieredDiskInterfaceRenderingProperties.getInactiveBaseModel(this.tier));
        resolver.markDependency(TieredDiskInterfaceRenderingProperties.getActiveBaseModel(this.tier, this.color));
        resolver.markDependency(TieredDiskInterfaceRenderingProperties.INACTIVE_LED_MODEL);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(resolver::markDependency);
    }
}
