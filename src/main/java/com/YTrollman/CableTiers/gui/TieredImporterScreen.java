package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blockentity.TieredImporterBlockEntity;
import com.YTrollman.CableTiers.container.TieredImporterContainerMenu;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.YTrollman.CableTiers.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class TieredImporterScreen extends TieredScreen<TieredImporterBlockEntity, TieredImporterContainerMenu, TieredImporterNetworkNode> {

    public TieredImporterScreen(TieredImporterContainerMenu container, Inventory inventory, Component title) {
        super(container, container.getTier() == CableTier.CREATIVE ? 176 : 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredImporterBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredImporterBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredImporterBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredImporterBlockEntity.COMPARE));
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(poseStack, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), new TranslatableComponent("container.inventory").getString());
    }
}
