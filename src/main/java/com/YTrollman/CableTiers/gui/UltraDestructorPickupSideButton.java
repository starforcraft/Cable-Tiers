package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.container.UltraDestructorContainer;
import com.YTrollman.CableTiers.tileentity.UltraDestructorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class UltraDestructorPickupSideButton extends SideButton {
    public UltraDestructorPickupSideButton(BaseScreen<UltraDestructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(UltraDestructorTileEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.destructor.pickup") + "\n" + TextFormatting.GRAY + I18n.format(Boolean.TRUE.equals(UltraDestructorTileEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(UltraDestructorTileEntity.PICKUP, !UltraDestructorTileEntity.PICKUP.getValue());
    }
}
