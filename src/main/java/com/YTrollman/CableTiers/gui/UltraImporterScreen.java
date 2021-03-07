package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.UltraImporterContainer;
import com.YTrollman.CableTiers.tileentity.UltraImporterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class UltraImporterScreen extends BaseScreen<UltraImporterContainer>
{
    public UltraImporterScreen(UltraImporterContainer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 211, 191, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y)
    {
        addSideButton(new RedstoneModeSideButton(this, UltraImporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, UltraImporterTileEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, UltraImporterTileEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, UltraImporterTileEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y)
    {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CableTiers.MOD_ID, "gui/ultra_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1)
    {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 96, new TranslationTextComponent("container.inventory").getString());
    }
}
