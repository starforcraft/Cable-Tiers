package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.container.UltraConstructorContainer;
import com.YTrollman.CableTiers.tileentity.UltraConstructorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class UltraConstructorScreen extends BaseScreen<UltraConstructorContainer> {
    public UltraConstructorScreen(UltraConstructorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, UltraConstructorTileEntity.TYPE));

        addSideButton(new ExactModeSideButton(this, UltraConstructorTileEntity.COMPARE));
        addSideButton(new UltraConstructorDropSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/constructor.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"));
    }
}
