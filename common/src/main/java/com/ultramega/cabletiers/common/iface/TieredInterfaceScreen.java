package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import static com.refinedmods.refinedstorage.common.support.Sprites.WARNING_SIZE;
import static com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen.renderTieredExportingIndicators;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class TieredInterfaceScreen extends AbstractBaseScreen<TieredInterfaceContainerMenu> {
    private static final Identifier ELITE_TEXTURE = createCableTiersIdentifier("textures/gui/elite_interface.png");
    private static final Identifier ULTRA_MEGA_CREATIVE_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_mega_creative_interface.png");

    private final CableTiers tier;

    public TieredInterfaceScreen(final TieredInterfaceContainerMenu menu, final Inventory playerInventory, final Component title, final CableTiers tier) {
        super(menu, playerInventory, title, 210, tier == CableTiers.ELITE ? 218 : 236);
        this.tier = tier;

        this.inventoryLabelY = 88 + 36 + (tier != CableTiers.ELITE ? 18 : 0);
    }

    @Override
    protected void extractSlot(final GuiGraphicsExtractor graphics, final Slot slot, final int mouseX, final int mouseY) {
        // I honestly have no idea why this is required for the exported resource resourceContents but not for the export config resourceContents
        if (this.getMenu().getExportedResourceSlots().contains(slot)) {
            return;
        }
        super.extractSlot(graphics, slot, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.addSideButton(new RedstoneModeSideButtonWidget(this.getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
        this.addSideButton(new FuzzyModeSideButtonWidget(
            this.getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
        ));
    }

    @Override
    protected void extractTooltip(final GuiGraphicsExtractor graphics, final int x, final int y) {
        if (renderTieredExportingIndicators(this.font, graphics, this.leftPos, this.topPos + WARNING_SIZE - 18 - 2, x, y,
            this.getMenu().getIndicators(), this.getMenu()::getIndicator)) {
            return;
        }
        super.extractTooltip(graphics, x, y);
    }

    @Override
    protected Identifier getTexture() {
        return this.tier == CableTiers.ELITE ? ELITE_TEXTURE : ULTRA_MEGA_CREATIVE_TEXTURE;
    }
}
