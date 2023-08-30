package com.ultramega.cabletiers.screen;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredImporterBlockEntity;
import com.ultramega.cabletiers.container.TieredImporterContainerMenu;
import com.ultramega.cabletiers.node.TieredImporterNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredImporterScreen extends TieredScreen<TieredImporterBlockEntity, TieredImporterContainerMenu, TieredImporterNetworkNode> {
    public TieredImporterScreen(TieredImporterContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredImporterBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredImporterBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredImporterBlockEntity.COMPARE));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_exporter_importer_destructor.png"), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), I18n.get("container.inventory"));
    }
}
