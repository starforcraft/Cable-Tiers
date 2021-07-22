package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.TieredExporterContainer;
import com.YTrollman.CableTiers.node.TieredExporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredExporterTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TieredExporterScreen extends TieredScreen<TieredExporterTileEntity, TieredExporterContainer, TieredExporterNetworkNode> {

    private boolean hasRegulatorMode;

    public TieredExporterScreen(TieredExporterContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(container.getTier().getSlots(), 9), inventory, title);
        this.hasRegulatorMode = hasRegulatorMode();
    }

    private boolean hasRegulatorMode() {
        return getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredExporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredExporterTileEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredExporterTileEntity.COMPARE));
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
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1) {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 24 + 18 * MathUtil.ceilDiv(getTier().getSlots(), 9), new TranslationTextComponent("container.inventory").getString());
    }
}
