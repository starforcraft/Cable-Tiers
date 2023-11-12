package com.ultramega.cabletiers.gui;

import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredExporterBlockEntity;
import com.ultramega.cabletiers.container.TieredExporterContainerMenu;
import com.ultramega.cabletiers.node.TieredExporterNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.network.chat.Component;
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
        addSideButton(new RedstoneModeSideButton(this, TieredExporterBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredExporterBlockEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredExporterBlockEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode;
            menu.checkRegulator();
        }
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int i, int i1) {
        renderString(poseStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(poseStack, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), Component.translatable("container.inventory").getString());
    }
}
