package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blockentity.TieredDestructorBlockEntity;
import com.YTrollman.CableTiers.container.TieredDestructorContainerMenu;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.YTrollman.CableTiers.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TieredDestructorScreen extends TieredScreen<TieredDestructorBlockEntity, TieredDestructorContainerMenu, TieredDestructorNetworkNode> {

    public TieredDestructorScreen(TieredDestructorContainerMenu container, Inventory inventory, Component title) {
        super(container, 211, 119 + 18 * MathUtil.ceilDiv(9 * container.getTier().getSlotsMultiplier(), 9), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredDestructorBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredDestructorBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredDestructorBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredDestructorBlockEntity.COMPARE));
        addSideButton(new TieredDestructorPickupSideButton(this));
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_exporter_importer_destructor.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 24 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9), I18n.get("container.inventory"));
    }

    private static class TieredDestructorPickupSideButton extends SideButton {

        private TieredDestructorPickupSideButton(BaseScreen<?> screen) {
            super(screen);
        }

        @Override
        protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
            screen.blit(poseStack, x, y, 64 + (Boolean.TRUE.equals(TieredDestructorBlockEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
        }

        @Override
        public String getTooltip() {
            return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(TieredDestructorBlockEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredDestructorBlockEntity.PICKUP, !TieredDestructorBlockEntity.PICKUP.getValue());
        }
    }
}
