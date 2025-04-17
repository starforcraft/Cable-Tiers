package com.ultramega.cabletiers.common.importer;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.storage.FilterModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredImporterScreen extends AbstractAdvancedFilterScreen<TieredImporterContainerMenu> {
    public TieredImporterScreen(final TieredImporterContainerMenu menu,
                                final Inventory playerInventory,
                                final Component title,
                                final CableTiers tier) {
        super(menu, playerInventory, title, tier);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new FilterModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FILTER_MODE),
            createTranslation("gui", "importer.filter_mode.allow.help"),
            createTranslation("gui", "importer.filter_mode.block.help")
        ));
        addSideButton(new FuzzyModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_SOURCE
        ));
    }
}
