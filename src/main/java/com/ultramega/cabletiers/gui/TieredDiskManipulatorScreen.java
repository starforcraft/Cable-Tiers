package com.ultramega.cabletiers.gui;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredDiskManipulatorBlockEntity;
import com.ultramega.cabletiers.container.TieredDiskManipulatorContainer;
import com.ultramega.cabletiers.node.diskmanipulator.TieredDiskManipulatorNetworkNode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TieredDiskManipulatorScreen extends TieredScreen<TieredDiskManipulatorBlockEntity, TieredDiskManipulatorContainer, TieredDiskManipulatorNetworkNode> {
    public TieredDiskManipulatorScreen(TieredDiskManipulatorContainer container, Inventory inventory, Component title) {
        super(container, 211, 245 + (container.getTier() == CableTier.MEGA ? 11 : 0), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TieredIoModeSideButton(this));
        addSideButton(new TypeSideButton(this, TieredDiskManipulatorBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredDiskManipulatorBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredDiskManipulatorBlockEntity.COMPARE));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_disk_manipulator.png"), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, screenText("Inv", true), I18n.get("container.inventory"));
        renderString(graphics, screenText("In", false), screenText("In", true), I18n.get("gui.refinedstorage.disk_manipulator.in"));
        renderString(graphics, screenText("Out", false), screenText("Out", true), I18n.get("gui.refinedstorage.disk_manipulator.out"));
    }

    private int screenText(String whatText, boolean isY) {
        if (getTier() == CableTier.ELITE) {
            switch (whatText) {
                case "In" -> {
                    return isY ? 60 : 39;
                }
                case "Out" -> {
                    return isY ? 60 : 125;
                }
                case "Inv" -> {
                    return 131;
                }
            }
        } else if (getTier() == CableTier.ULTRA) {
            switch (whatText) {
                case "In" -> {
                    return isY ? 78 : 29;
                }
                case "Out" -> {
                    return isY ? 78 : 134;
                }
                case "Inv" -> {
                    return 149;
                }
            }
        } else if (getTier() == CableTier.MEGA) {
            switch (whatText) {
                case "In" -> {
                    return isY ? 95 : 35;
                }
                case "Out" -> {
                    return isY ? 95 : 128;
                }
                case "Inv" -> {
                    return 163;
                }
            }
        }
        return 0;
    }

    public class TieredIoModeSideButton extends SideButton {
        public TieredIoModeSideButton(BaseScreen<TieredDiskManipulatorContainer> screen) {
            super(screen);
        }

        @Override
        public String getSideButtonTooltip() {
            return I18n.get("sidebutton.refinedstorage.iomode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.iomode." + (TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_INSERT ? "insert" : "extract"));
        }

        @Override
        protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
            graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredDiskManipulatorBlockEntity.IO_MODE, TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_INSERT ? TieredDiskManipulatorNetworkNode.IO_MODE_EXTRACT : TieredDiskManipulatorNetworkNode.IO_MODE_INSERT);
        }
    }
}
