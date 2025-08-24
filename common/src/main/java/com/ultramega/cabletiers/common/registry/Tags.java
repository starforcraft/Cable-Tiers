package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public final class Tags {
    private static final Map<CableTiers, Map<CableType, TagKey<Item>>> CONTENT_MAP_ITEMS = new EnumMap<>(CableTiers.class);
    private static final Map<CableTiers, Map<CableType, TagKey<Block>>> CONTENT_MAP_BLOCKS = new EnumMap<>(CableTiers.class);

    private Tags() {
    }

    public static TagKey<Item> getItemTag(final CableTiers tier, final CableType type) {
        return CONTENT_MAP_ITEMS.getOrDefault(tier, Map.of()).get(type);
    }

    public static TagKey<Block> getBlockTag(final CableTiers tier, final CableType type) {
        return CONTENT_MAP_BLOCKS.getOrDefault(tier, Map.of()).get(type);
    }

    private static <T> TagKey<T> createTag(final ResourceKey<? extends Registry<T>> registry, final String id) {
        return TagKey.create(registry, createCableTiersIdentifier(id));
    }

    static {
        for (final CableTiers tier : CableTiers.values()) {
            final Map<CableType, TagKey<Item>> itemsMap = new EnumMap<>(CableType.class);
            final Map<CableType, TagKey<Block>> blocksMap = new EnumMap<>(CableType.class);
            for (final CableType type : CableType.values()) {
                final String id = tier.name().toLowerCase(Locale.ROOT) + "_" + type.name().toLowerCase(Locale.ROOT) + "s";
                itemsMap.put(type, createTag(Registries.ITEM, id));
                blocksMap.put(type, createTag(Registries.BLOCK, id));
            }
            CONTENT_MAP_ITEMS.put(tier, itemsMap);
            CONTENT_MAP_BLOCKS.put(tier, blocksMap);
        }
    }
}
