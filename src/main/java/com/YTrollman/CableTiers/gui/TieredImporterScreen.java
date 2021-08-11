package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.TieredImporterContainer;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredImporterTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TieredImporterScreen extends TieredScreen<TieredImporterTileEntity, TieredImporterContainer, TieredImporterNetworkNode> {

    public TieredImporterScreen(TieredImporterContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, container.getTier() == CableTier.CREATIVE ? 176 : 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredImporterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredImporterTileEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredImporterTileEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredImporterTileEntity.COMPARE));
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), new TranslationTextComponent("container.inventory").getString());
    }
}
