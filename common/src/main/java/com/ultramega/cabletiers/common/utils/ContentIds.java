package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public final class ContentIds {
    private static final Map<CableTiers, Map<CableType, ResourceLocation>> CONTENT_MAP = new EnumMap<>(CableTiers.class);

    private ContentIds() {
    }

    public static ResourceLocation getContentId(final CableTiers tier, final CableType type) {
        return CONTENT_MAP.getOrDefault(tier, Map.of()).get(type);
    }

    static {
        for (final CableTiers tier : CableTiers.values()) {
            final Map<CableType, ResourceLocation> map = new EnumMap<>(CableType.class);
            for (final CableType type : CableType.values()) {
                map.put(type, createCableTiersIdentifier(tier.name().toLowerCase(Locale.ROOT) + "_" + type.name().toLowerCase(Locale.ROOT)));
            }
            CONTENT_MAP.put(tier, map);
        }
    }
}
