package com.ultramega.cabletiers.gui;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredDestructorBlockEntity;
import com.ultramega.cabletiers.container.TieredDestructorContainerMenu;
import com.ultramega.cabletiers.node.TieredDestructorNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredDestructorScreen extends TieredScreen<TieredDestructorBlockEntity, TieredDestructorContainerMenu, TieredDestructorNetworkNode> {
    public TieredDestructorScreen(TieredDestructorContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredDestructorBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredDestructorBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredDestructorBlockEntity.COMPARE));
        addSideButton(new TieredDestructorPickupSideButton(this));
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

    private static class TieredDestructorPickupSideButton extends SideButton {
        private TieredDestructorPickupSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
            graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 64 + (Boolean.TRUE.equals(TieredDestructorBlockEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
        }

        @Override
        public String getSideButtonTooltip() {
            return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredDestructorBlockEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredDestructorBlockEntity.PICKUP, !TieredDestructorBlockEntity.PICKUP.getValue());
        }
    }
}
