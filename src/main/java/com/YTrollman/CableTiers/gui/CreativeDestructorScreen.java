package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.CreativeDestructorContainer;
import com.YTrollman.CableTiers.tileentity.CreativeDestructorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CreativeDestructorScreen extends BaseScreen<CreativeDestructorContainer> {
    public CreativeDestructorScreen(CreativeDestructorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 227, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, CreativeDestructorTileEntity.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, CreativeDestructorTileEntity.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, CreativeDestructorTileEntity.COMPARE));

        addSideButton(new CreativeDestructorPickupSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/creative_exporter_importer_destructor.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 132, I18n.get("container.inventory"));
    }
}
