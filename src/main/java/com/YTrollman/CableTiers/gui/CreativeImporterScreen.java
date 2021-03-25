package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.CreativeImporterContainer;
import com.YTrollman.CableTiers.tileentity.CreativeImporterTileEntity;
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

public class CreativeImporterScreen extends BaseScreen<CreativeImporterContainer>
{
    public CreativeImporterScreen(CreativeImporterContainer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 211, 227, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y)
    {
        addSideButton(new RedstoneModeSideButton(this, CreativeImporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, CreativeImporterTileEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, CreativeImporterTileEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, CreativeImporterTileEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y)
    {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CableTiers.MOD_ID, "gui/creative_importer.png");
        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1)
    {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 132, new TranslationTextComponent("container.inventory").getString());
    }
}
