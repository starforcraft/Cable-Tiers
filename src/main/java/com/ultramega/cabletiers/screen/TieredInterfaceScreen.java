package com.ultramega.cabletiers.screen;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredInterfaceBlockEntity;
import com.ultramega.cabletiers.container.TieredInterfaceContainerMenu;
import com.ultramega.cabletiers.node.TieredInterfaceNetworkNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredInterfaceScreen extends TieredScreen<TieredInterfaceBlockEntity, TieredInterfaceContainerMenu, TieredInterfaceNetworkNode> {
    public TieredInterfaceScreen(TieredInterfaceContainerMenu container, Inventory inventory, Component title) {
        super(container, container.getTier() == CableTier.ELITE ? 211 : 247, 267, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new ExactModeSideButton(this, TieredInterfaceBlockEntity.COMPARE));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_interface.png"), x, y, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, I18n.get("gui.cabletiers." + getTier().getName() + "_interface.import"));
        renderString(graphics, 7, 60, I18n.get("gui.cabletiers." + getTier().getName() + "_interface.export"));
        renderString(graphics, 7, 172, I18n.get("container.inventory"));
    }
}
