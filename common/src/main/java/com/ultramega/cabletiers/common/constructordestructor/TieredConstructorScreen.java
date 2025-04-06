package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.AbstractYesNoSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SchedulingModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredConstructorScreen extends AbstractAdvancedFilterScreen<TieredConstructorContainerMenu> {
    public TieredConstructorScreen(final TieredConstructorContainerMenu menu,
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
        addSideButton(new ConstructorDropItemsSideButtonWidget(
            getMenu().getProperty(ConstructorDestructorPropertyTypes.DROP_ITEMS)
        ));
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (renderTieredExportingIndicators(graphics, leftPos, topPos, x, y, getMenu().getIndicators(), getMenu()::getIndicator)) {
            return;
        }
        super.renderTooltip(graphics, x, y);
    }

    /**
     * Exact copy of {@link com.refinedmods.refinedstorage.common.constructordestructor.ConstructorDropItemsSideButtonWidget}
     */
    static class ConstructorDropItemsSideButtonWidget extends AbstractYesNoSideButtonWidget {
        private static final MutableComponent TITLE = createTranslation("gui", "constructor.drop_items");
        private static final ResourceLocation YES = createIdentifier("widget/side_button/constructor_drop_items/yes");
        private static final ResourceLocation NO = createIdentifier("widget/side_button/constructor_drop_items/no");

        ConstructorDropItemsSideButtonWidget(final ClientProperty<Boolean> property) {
            super(property, TITLE, YES, NO);
        }
    }
}
