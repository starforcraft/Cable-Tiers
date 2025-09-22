package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.support.tooltip.SmallText;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputScreen.getDirectionName;

public final class ClientUtils {
    private ClientUtils() {
    }

    public static void renderDirectionText(final GuiGraphics graphics, final Font font, @Nullable final Direction direction, final int x, final int y) {
        if (direction == null) {
            return;
        }

        final Component name = getDirectionName(direction);
        final String shortName = name.getString().substring(0, 1);

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 200.0F);
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
        graphics.pose().popPose();
    }
}
