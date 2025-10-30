package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import static com.refinedmods.refinedstorage.common.support.Sprites.WARNING_SIZE;
import static com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen.renderTieredExportingIndicators;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class TieredInterfaceScreen extends AbstractBaseScreen<TieredInterfaceContainerMenu> {
    private static final ResourceLocation ELITE_TEXTURE = createCableTiersIdentifier("textures/gui/elite_interface.png");
    private static final ResourceLocation ULTRA_MEGA_CREATIVE_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_mega_creative_interface.png");

    private final CableTiers tier;

    public TieredInterfaceScreen(final TieredInterfaceContainerMenu menu, final Inventory playerInventory, final Component title, final CableTiers tier) {
        super(menu, playerInventory, title);
        this.tier = tier;

        this.inventoryLabelY = 88 + 36 + (tier != CableTiers.ELITE ? 18 : 0);
        this.imageWidth = 210;
        this.imageHeight = tier == CableTiers.ELITE ? 218 : 236;
    }

    @Override
    protected void renderSlot(final GuiGraphics guiGraphics, final Slot slot) {
        // I honestly have no idea why this is required for the exported resource slots but not for the export config slots
        if (getMenu().getExportedResourceSlots().contains(slot)) {
            return;
        }
        super.renderSlot(guiGraphics, slot);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
        addSideButton(new FuzzyModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
        ));
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (renderTieredExportingIndicators(graphics, leftPos, topPos + WARNING_SIZE - 18 - 2, x, y,
            getMenu().getIndicators(), getMenu()::getIndicator)) {
            return;
        }
        super.renderTooltip(graphics, x, y);
    }

    @Override
    protected ResourceLocation getTexture() {
        return tier == CableTiers.ELITE ? ELITE_TEXTURE : ULTRA_MEGA_CREATIVE_TEXTURE;
    }
}
