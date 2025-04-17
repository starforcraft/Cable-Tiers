package com.ultramega.cabletiers.common.exporter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SchedulingModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TieredExporterScreen extends AbstractAdvancedFilterScreen<TieredExporterContainerMenu> {
    public TieredExporterScreen(final TieredExporterContainerMenu menu,
                                final Inventory playerInventory,
                                final Component title,
                                final CableTiers tier) {
        super(menu, playerInventory, title, tier);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new FuzzyModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
        ));
        addSideButton(new SchedulingModeSideButtonWidget(getMenu().getProperty(PropertyTypes.SCHEDULING_MODE)));
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (renderTieredExportingIndicators(graphics, leftPos, topPos, x, y, getMenu().getIndicators(), getMenu()::getIndicator)) {
            return;
        }
        super.renderTooltip(graphics, x, y);
    }
}
