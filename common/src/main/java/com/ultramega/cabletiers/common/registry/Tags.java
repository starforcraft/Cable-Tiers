package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public final class Tags {
    private static final Map<CableTiers, Map<CableType, TagKey<Item>>> CONTENT_MAP = new EnumMap<>(CableTiers.class);

    private Tags() {
    }

    public static TagKey<Item> getTag(final CableTiers tier, final CableType type) {
        return CONTENT_MAP.getOrDefault(tier, Map.of()).get(type);
    }

    private static TagKey<Item> createTag(final String id) {
        return TagKey.create(Registries.ITEM, createCableTiersIdentifier(id));
    }

    static {
        for (final CableTiers tier : CableTiers.values()) {
            final Map<CableType, TagKey<Item>> map = new EnumMap<>(CableType.class);
            for (final CableType type : CableType.values()) {
                map.put(type, createTag(tier.name().toLowerCase() + "_" + type.name().toLowerCase() + "s"));
            }
            CONTENT_MAP.put(tier, map);
        }
    }
}
