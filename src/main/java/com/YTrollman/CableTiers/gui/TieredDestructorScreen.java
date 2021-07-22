package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.TieredDestructorContainer;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredDestructorTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class TieredDestructorScreen extends TieredScreen<TieredDestructorTileEntity, TieredDestructorContainer, TieredDestructorNetworkNode> {

    public TieredDestructorScreen(TieredDestructorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(container.getTier().getSlots(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredDestructorTileEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredDestructorTileEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredDestructorTileEntity.COMPARE));
        addSideButton(new TieredDestructorPickupSideButton(this));
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 24 + 18 * MathUtil.ceilDiv(getTier().getSlots(), 9), I18n.get("container.inventory"));
    }

    private static class TieredDestructorPickupSideButton extends SideButton {

        private TieredDestructorPickupSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
            screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(TieredDestructorTileEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
        }

        @Override
        public String getTooltip() {
            return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + TextFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredDestructorTileEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            TileDataManager.setParameter(TieredDestructorTileEntity.PICKUP, !TieredDestructorTileEntity.PICKUP.getValue());
        }
    }
}
