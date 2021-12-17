package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blockentity.TieredConstructorBlockEntity;
import com.YTrollman.CableTiers.container.TieredConstructorContainerMenu;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TieredConstructorScreen extends TieredScreen<TieredConstructorBlockEntity, TieredConstructorContainerMenu, TieredConstructorNetworkNode> {

    public TieredConstructorScreen(TieredConstructorContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredConstructorBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredConstructorBlockEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredConstructorBlockEntity.COMPARE));
        addSideButton(new TieredConstructorDropSideButton(this));
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_constructor.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 42, I18n.get("container.inventory"));
    }

    private static class TieredConstructorDropSideButton extends SideButton {

        private TieredConstructorDropSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
            screen.blit(poseStack, x, y, 64 + (Boolean.TRUE.equals(TieredConstructorBlockEntity.DROP.getValue()) ? 16 : 0), 16, 16, 16);
        }

        @Override
        public String getTooltip() {
            return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredConstructorBlockEntity.DROP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredConstructorBlockEntity.DROP, !TieredConstructorBlockEntity.DROP.getValue());
        }
    }
}
