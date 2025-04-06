package com.ultramega.cabletiers.neoforge.datagen;

import com.ultramega.cabletiers.common.CableTiers;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

class TieredColoredCustomLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private final DyeColor color;
    private final CableTiers tier;

    TieredColoredCustomLoaderBuilder(final ResourceLocation loaderId,
                                     final T parent,
                                     final ExistingFileHelper existingFileHelper,
                                     final DyeColor color,
                                     final CableTiers tier) {
        super(loaderId, parent, existingFileHelper, true);
        this.color = color;
        this.tier = tier;
    }

    @Override
    public JsonObject toJson(final JsonObject json) {
        final JsonObject value = super.toJson(json);
        value.addProperty("color", color.getName());
        value.addProperty("tier", tier.toString().toLowerCase());
        return value;
    }
}
