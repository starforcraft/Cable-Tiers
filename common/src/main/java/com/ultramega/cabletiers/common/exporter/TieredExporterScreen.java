package com.ultramega.cabletiers.common.exporter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SchedulingModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TieredExporterScreen extends AbstractAdvancedFilterScreen<TieredExporterContainerMenu> {
    public TieredExporterScreen(final TieredExporterContainerMenu menu,
                                final Inventory playerInventory,
                                final Component title,
                                final CableTiers tier) {
        super(menu, playerInventory, title, tier, true);
    }

    @Override
    protected void init() {
        super.init();
        this.addSideButton(new FuzzyModeSideButtonWidget(
            this.getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
        ));
        this.addSideButton(new SchedulingModeSideButtonWidget(this.getMenu().getProperty(PropertyTypes.SCHEDULING_MODE)));
    }

    @Override
    protected void extractTooltip(final GuiGraphicsExtractor graphics, final int x, final int y) {
        if (renderTieredExportingIndicators(this.font, graphics, this.leftPos, this.topPos, x, y, this.getMenu().getIndicators(), this.getMenu()::getIndicator)) {
            return;
        }
        super.extractTooltip(graphics, x, y);
    }
}
