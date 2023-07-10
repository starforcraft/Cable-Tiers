package com.ultramega.cabletiers.gui;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredExporterBlockEntity;
import com.ultramega.cabletiers.container.TieredExporterContainerMenu;
import com.ultramega.cabletiers.node.TieredExporterNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredExporterScreen extends TieredScreen<TieredExporterBlockEntity, TieredExporterContainerMenu, TieredExporterNetworkNode> {
    private boolean hasRegulatorMode;

    public TieredExporterScreen(TieredExporterContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
        this.hasRegulatorMode = hasRegulatorMode();
    }

    private boolean hasRegulatorMode() {
        return getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredExporterBlockEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredExporterBlockEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode;

            menu.initSlots();
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_exporter_importer_destructor.png"), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int i, int i1) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), I18n.get("container.inventory"));
    }
}
