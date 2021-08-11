package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.TieredConstructorContainer;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredConstructorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class TieredConstructorScreen extends TieredScreen<TieredConstructorTileEntity, TieredConstructorContainer, TieredConstructorNetworkNode> {

    public TieredConstructorScreen(TieredConstructorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredConstructorTileEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredConstructorTileEntity.COMPARE));
        addSideButton(new TieredConstructorDropSideButton(this));
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_constructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 42, I18n.get("container.inventory"));
    }

    private static class TieredConstructorDropSideButton extends SideButton {

        private TieredConstructorDropSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
            screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(TieredConstructorTileEntity.DROP.getValue()) ? 16 : 0), 16, 16, 16);
        }

        @Override
        public String getTooltip() {
            return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + TextFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredConstructorTileEntity.DROP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            TileDataManager.setParameter(TieredConstructorTileEntity.DROP, !TieredConstructorTileEntity.DROP.getValue());
        }
    }
}
