package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.neoforge.support.render.DiskModelBaker;
import com.refinedmods.refinedstorage.neoforge.support.render.RotationTranslationModelBaker;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static java.util.Objects.requireNonNull;

public class TieredDiskInterfaceUnbakedGeometry implements IUnbakedGeometry<TieredDiskInterfaceUnbakedGeometry> {
    private static final ResourceLocation LED_INACTIVE_MODEL = createIdentifier("block/disk/led_inactive");

    private final ResourceLocation baseModel;
    private final ResourceLocation inactiveModel;

    TieredDiskInterfaceUnbakedGeometry(final DyeColor color, final CableTiers tier) {
        this.baseModel = createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/base_" + color.getName());
        this.inactiveModel = createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/inactive");
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter,
                               final IGeometryBakingContext context) {
        modelGetter.apply(baseModel).resolveParents(modelGetter);
        modelGetter.apply(inactiveModel).resolveParents(modelGetter);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(
            diskModel -> modelGetter.apply(diskModel).resolveParents(modelGetter)
        );
        modelGetter.apply(LED_INACTIVE_MODEL).resolveParents(modelGetter);
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context,
                           final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ItemOverrides overrides) {
        return new TieredDiskInterfaceBakedModel(
            requireNonNull(baker.bake(baseModel, modelState, spriteGetter)),
            new RotationTranslationModelBaker(modelState, baker, spriteGetter, inactiveModel),
            new RotationTranslationModelBaker(modelState, baker, spriteGetter, baseModel),
            new DiskModelBaker(modelState, baker, spriteGetter),
            new RotationTranslationModelBaker(modelState, baker, spriteGetter, LED_INACTIVE_MODEL)
        );
    }
}
