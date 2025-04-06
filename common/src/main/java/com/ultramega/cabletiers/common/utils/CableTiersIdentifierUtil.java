package com.ultramega.cabletiers.common.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public final class CableTiersIdentifierUtil {
    public static final String MOD_ID = "cabletiers";

    private CableTiersIdentifierUtil() {
    }

    public static ResourceLocation createCableTiersIdentifier(final String value) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, value);
    }

    public static MutableComponent createCableTiersTranslation(final String category, final String value) {
        return Component.translatable(createCableTiersTranslationKey(category, value));
    }

    public static String createCableTiersTranslationKey(final String category, final String value) {
        return String.format("%s.%s.%s", category, MOD_ID, value);
    }

    public static MutableComponent createCableTiersTranslationAsHeading(final String category, final String value) {
        return Component.literal("<")
            .append(createCableTiersTranslation(category, value))
            .append(">")
            .withStyle(ChatFormatting.DARK_GRAY);
    }
}
