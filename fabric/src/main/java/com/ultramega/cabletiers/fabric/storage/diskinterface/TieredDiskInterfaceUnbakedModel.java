package com.ultramega.cabletiers.fabric.storage.diskinterface;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.fabric.support.render.QuadRotators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static java.util.Objects.requireNonNull;

public class TieredDiskInterfaceUnbakedModel implements UnbakedModel {
    private static final ResourceLocation INACTIVE_MODEL = createIdentifier("block/disk_interface/inactive");
    private static final ResourceLocation LED_INACTIVE_MODEL = createIdentifier("block/disk/led_inactive");

    private final QuadRotators quadRotators;
    private final ResourceLocation baseModel;
    private final ResourceLocation emissiveSprite;

    public TieredDiskInterfaceUnbakedModel(final QuadRotators quadRotators, final DyeColor color) {
        this.quadRotators = quadRotators;
        this.baseModel = createIdentifier("block/disk_interface/base_" + color.getName());
        this.emissiveSprite = createIdentifier("block/disk_interface/cutouts/" + color.getName());
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        final Set<ResourceLocation> dependencies = new HashSet<>();
        dependencies.add(baseModel);
        dependencies.add(INACTIVE_MODEL);
        dependencies.add(LED_INACTIVE_MODEL);
        dependencies.addAll(RefinedStorageClientApi.INSTANCE.getDiskModels());
        return dependencies;
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter) {
        modelGetter.apply(baseModel).resolveParents(modelGetter);
        modelGetter.apply(INACTIVE_MODEL).resolveParents(modelGetter);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(
            diskModel -> modelGetter.apply(diskModel).resolveParents(modelGetter)
        );
        modelGetter.apply(LED_INACTIVE_MODEL).resolveParents(modelGetter);
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState state) {
        final Map<Item, BakedModel> diskModels = RefinedStorageClientApi.INSTANCE
            .getDiskModelsByItem()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> requireNonNull(baker.bake(entry.getValue(), state))
            ));
        return new TieredDiskInterfaceBakedModel(
            requireNonNull(baker.bake(baseModel, state)),
            requireNonNull(baker.bake(INACTIVE_MODEL, state)),
            diskModels,
            requireNonNull(baker.bake(LED_INACTIVE_MODEL, state)),
            quadRotators,
            emissiveSprite
        );
    }
}
