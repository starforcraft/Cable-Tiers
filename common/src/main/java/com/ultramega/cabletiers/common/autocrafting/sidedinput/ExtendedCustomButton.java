package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.CustomButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ExtendedCustomButton extends CustomButton {
    private final Component componentHelp;

    public ExtendedCustomButton(final int x,
                                final int y,
                                final int width,
                                final int height,
                                final WidgetSprites sprites,
                                final Consumer<CustomButton> onPress,
                                final Component component,
                                final Component componentHelp) {
        super(x, y, width, height, sprites, onPress, component);
        this.componentHelp = componentHelp;
    }

    @Override
    public void renderWidget(final GuiGraphics graphics, final int x, final int y, final float partialTicks) {
        super.renderWidget(graphics, x, y, partialTicks);

        if (isHovered()) {
            final Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof AbstractBaseScreen<?> baseScreen) {
                baseScreen.setDeferredTooltip(buildTooltip());
            }
        }
    }

    protected List<ClientTooltipComponent> buildTooltip() {
        final List<ClientTooltipComponent> lines = new ArrayList<>();
        lines.add(ClientTooltipComponent.create(this.getMessage().getVisualOrderText()));
        lines.add(HelpClientTooltipComponent.create(this.componentHelp));
        return lines;
    }
}
