package com.ultramega.cabletiers.screen;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredConstructorBlockEntity;
import com.ultramega.cabletiers.container.TieredConstructorContainerMenu;
import com.ultramega.cabletiers.node.TieredConstructorNetworkNode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredConstructorScreen extends TieredScreen<TieredConstructorBlockEntity, TieredConstructorContainerMenu, TieredConstructorNetworkNode> {
    public TieredConstructorScreen(TieredConstructorContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredConstructorBlockEntity.TYPE));
        addSideButton(new ExactModeSideButton(this, TieredConstructorBlockEntity.COMPARE));
        addSideButton(new TieredConstructorDropSideButton(this));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_constructor.png"), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 42, I18n.get("container.inventory"));
    }

    private static class TieredConstructorDropSideButton extends SideButton {
        private TieredConstructorDropSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
            graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 64 + (Boolean.TRUE.equals(TieredConstructorBlockEntity.DROP.getValue()) ? 16 : 0), 16, 16, 16);
        }

        @Override
        public String getSideButtonTooltip() {
            return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredConstructorBlockEntity.DROP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredConstructorBlockEntity.DROP, !TieredConstructorBlockEntity.DROP.getValue());
        }
    }
}
