package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.UltraExporterContainer;
import com.YTrollman.CableTiers.tileentity.UltraExporterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class UltraExporterScreen extends BaseScreen<UltraExporterContainer>
{
    private boolean hasRegulatorMode;

    public UltraExporterScreen(UltraExporterContainer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 211, 191, inventory, title);
        this.hasRegulatorMode = hasRegulatorMode();
    }

    private boolean hasRegulatorMode()
    {
        return menu.getTile().getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void onPostInit(int x, int y)
    {
        addSideButton(new RedstoneModeSideButton(this, UltraExporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, UltraExporterTileEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, UltraExporterTileEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y)
    {
        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode)
        {
            hasRegulatorMode = updatedHasRegulatorMode;
            menu.initSlots();
        }
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(CableTiers.MOD_ID, "gui/ultra_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int i, int i1)
    {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 96, new TranslationTextComponent("container.inventory").getString());
    }
}
