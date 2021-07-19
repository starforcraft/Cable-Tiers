package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.EliteImporterContainer;
import com.YTrollman.CableTiers.tileentity.EliteImporterTileEntity;
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

public class EliteImporterScreen extends BaseScreen<EliteImporterContainer>
{
    public EliteImporterScreen(EliteImporterContainer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 211, 155, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y)
    {
        addSideButton(new RedstoneModeSideButton(this, EliteImporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, EliteImporterTileEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, EliteImporterTileEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, EliteImporterTileEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y)
    {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CableTiers.MOD_ID, "gui/elite_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1)
    {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 60, new TranslationTextComponent("container.inventory").getString());
    }
}
