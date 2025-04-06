package com.ultramega.cabletiers.common.support;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallTextClientTooltipComponent;

import java.util.List;
import java.util.function.IntFunction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.support.Sprites.WARNING_SIZE;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslationAsHeading;

public class AbstractAdvancedFilterScreen<T extends AbstractTieredFilterContainerMenu<?>> extends AbstractFilterScreen<T> {
    private static final ResourceLocation ELITE_TEXTURE = createCableTiersIdentifier("textures/gui/elite_generic.png");
    private static final ResourceLocation ULTRA_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_generic.png");
    private static final ResourceLocation MEGA_TEXTURE = createCableTiersIdentifier("textures/gui/mega_generic.png");

    private static final SmallTextClientTooltipComponent CLICK_TO_OPEN_ADVANCED_FILTER = new SmallTextClientTooltipComponent(
        createCableTiersTranslationAsHeading("gui", "filter_slot.click_to_open_advanced_filter"));
    private static final SmallTextClientTooltipComponent SHIFT_CLICK_TO_CLEAR = new SmallTextClientTooltipComponent(
        createTranslationAsHeading("gui", "filter_slot.shift_click_to_clear"));

    protected final CableTiers tier;

    protected AbstractAdvancedFilterScreen(final T menu,
                                           final Inventory playerInventory,
                                           final Component title,
                                           final CableTiers tier) {
        super(menu, playerInventory, title);
        this.tier = tier;

        switch (tier) {
            case ELITE:
                this.inventoryLabelY = 42 + 18;
                this.imageHeight = 155;
                break;
            case ULTRA:
                this.inventoryLabelY = 42 + 18 * 3;
                this.imageHeight = 191;
                break;
            case MEGA, CREATIVE:
                this.inventoryLabelY = 42 + 18 * 5;
                this.imageHeight = 227;
                break;
        }
        this.imageWidth = hasUpgrades() ? 210 : 176;
    }

    @Override
    protected ResourceLocation getTexture() {
        return switch (tier) {
            case ELITE -> ELITE_TEXTURE;
            case ULTRA -> ULTRA_TEXTURE;
            case MEGA, CREATIVE -> MEGA_TEXTURE;
        };
    }

    @Override
    protected void addResourceSlotTooltips(final ResourceSlot slot, final List<ClientTooltipComponent> tooltip) {
        if (getMenu().getTagKeys() != null) {
            final ResourceTag resourceTag = getMenu().getTagKeys().get(slot.index);
            if (resourceTag != null) {
                tooltip.add(new SmallTextClientTooltipComponent(Component.translatable("gui.cabletiers.advanced_filter.tag_filter")
                    .append(": ")
                    .append(Component.literal(resourceTag.key().location().toString()))
                    .withStyle(ChatFormatting.GOLD)));
            }
        }
        tooltip.add(CLICK_TO_OPEN_ADVANCED_FILTER);
        tooltip.add(SHIFT_CLICK_TO_CLEAR);
    }

    public static boolean renderTieredExportingIndicators(final GuiGraphics graphics,
                                                          final int leftPos,
                                                          final int topPos,
                                                          final int mouseX,
                                                          final int mouseY,
                                                          final int indicators,
                                                          final IntFunction<ExportingIndicator> indicatorProvider) {
        for (int i = 0; i < indicators; ++i) {
            final ExportingIndicator indicator = indicatorProvider.apply(i);
            final int xx = leftPos + 7 + (18 * (i % 9)) + 18 - 10 + 1;
            final int yy = topPos + 19 + 18 + (18 * (i / 9)) - 10 + 1;
            final ResourceLocation sprite = indicator.getSprite();
            if (sprite != null) {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 300);
                graphics.blitSprite(sprite, xx, yy, WARNING_SIZE, WARNING_SIZE);
                graphics.pose().popPose();
            }
            final boolean hovering =
                mouseX >= xx && mouseX <= xx + WARNING_SIZE && mouseY >= yy && mouseY <= yy + WARNING_SIZE;
            if (indicator != ExportingIndicator.NONE && hovering) {
                Platform.INSTANCE.renderTooltip(
                    graphics,
                    List.of(ClientTooltipComponent.create(indicator.getTooltip().getVisualOrderText())),
                    mouseX,
                    mouseY
                );
                return true;
            }
        }
        return false;
    }
}
