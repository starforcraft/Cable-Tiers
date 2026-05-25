package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.support.tooltip.SmallText;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import static com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputScreen.getDirectionName;

public final class ClientUtils {
    private ClientUtils() {
    }

    public static void renderDirectionText(final GuiGraphicsExtractor graphics, final Font font, @Nullable final Direction direction, final int x, final int y) {
        if (direction == null) {
            return;
        }

        final Component name = getDirectionName(direction);
        final String shortName = name.getString().substring(0, 1);

        SmallText.render(
            graphics,
            font,
            Component.literal(shortName).getVisualOrderText(),
            x + 19 - font.width(shortName),
            y,
            0xFFFFFFFF,
            true,
            SmallText.DEFAULT_SCALE
        );
    }
}
