package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.network.chat.MutableComponent;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public final class ContentNames {
    private static final Map<CableTiers, Map<CableType, MutableComponent>> CONTENT_MAP = new EnumMap<>(CableTiers.class);

    private ContentNames() {
    }

    public static MutableComponent getContentName(final CableTiers tier, final CableType type) {
        return CONTENT_MAP.getOrDefault(tier, Map.of()).get(type);
    }

    private static MutableComponent name(final String name) {
        return createCableTiersTranslation("block", name);
    }

    static {
        for (final CableTiers tier : CableTiers.values()) {
            final Map<CableType, MutableComponent> map = new EnumMap<>(CableType.class);
            for (final CableType type : CableType.values()) {
                map.put(type, name(tier.name().toLowerCase(Locale.ROOT) + "_" + type.name().toLowerCase(Locale.ROOT)));
            }
            CONTENT_MAP.put(tier, map);
        }
    }
}
