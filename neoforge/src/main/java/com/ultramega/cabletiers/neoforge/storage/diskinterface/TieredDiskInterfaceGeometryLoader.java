package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import static com.refinedmods.refinedstorage.common.content.Blocks.COLOR;

public class TieredDiskInterfaceGeometryLoader implements IGeometryLoader<TieredDiskInterfaceUnbakedGeometry> {
    @Override
    public TieredDiskInterfaceUnbakedGeometry read(final JsonObject jsonObject,
                                                   final JsonDeserializationContext deserializationContext) {
        final String color = jsonObject.get("color").getAsString();
        final DyeColor dyeColor = DyeColor.byName(color, COLOR);
        final String tier = jsonObject.get("tier").getAsString();
        final CableTiers cableTier = CableTiers.byName(tier);
        assert cableTier != null;
        return new TieredDiskInterfaceUnbakedGeometry(dyeColor, cableTier);
    }
}
