package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.container.EliteConstructorContainer;
import com.YTrollman.CableTiers.tileentity.EliteConstructorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class EliteConstructorDropSideButton extends SideButton {
    public EliteConstructorDropSideButton(BaseScreen<EliteConstructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(EliteConstructorTileEntity.DROP.getValue()) ? 16 : 0), 16, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + TextFormatting.GRAY + I18n.get(Boolean.TRUE.equals(EliteConstructorTileEntity.DROP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(EliteConstructorTileEntity.DROP, !EliteConstructorTileEntity.DROP.getValue());
    }
}
