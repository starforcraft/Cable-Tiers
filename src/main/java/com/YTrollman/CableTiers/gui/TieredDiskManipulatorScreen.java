package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blockentity.TieredDiskManipulatorBlockEntity;
import com.YTrollman.CableTiers.container.TieredDiskManipulatorContainer;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TieredDiskManipulatorScreen extends TieredScreen<TieredDiskManipulatorBlockEntity, TieredDiskManipulatorContainer, TieredDiskManipulatorNetworkNode> {

    public TieredDiskManipulatorScreen(TieredDiskManipulatorContainer container, Inventory inventory, Component title) {
        super(container, container.getTier() == CableTier.CREATIVE ? 176 : 211, 245 + (container.getTier() == CableTier.CREATIVE ? 11 : 0), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredDiskManipulatorBlockEntity.REDSTONE_MODE));
        addSideButton(new TieredIoModeSideButton(this));
        addSideButton(new TypeSideButton(this, TieredDiskManipulatorBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, TieredDiskManipulatorBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, TieredDiskManipulatorBlockEntity.COMPARE));
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_disk_manipulator.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, screenText("Inv", true), I18n.get("container.inventory"));
        renderString(poseStack, screenText("In", false), screenText("In", true), I18n.get("gui.refinedstorage.disk_manipulator.in"));
        renderString(poseStack, screenText("Out", false), screenText("Out", true), I18n.get("gui.refinedstorage.disk_manipulator.out"));
    }

    private int screenText(String whatText, boolean isY) {
        if (getTier() == CableTier.ELITE) {
            if (whatText.equals("In")) {
                if (isY) {
                    return 60;
                } else {
                    return 39;
                }
            } else if (whatText.equals("Out")) {
                if (isY) {
                    return 60;
                } else {
                    return 125;
                }
            } else if (whatText.equals("Inv")) {
                return 131;
            }
        } else if (getTier() == CableTier.ULTRA) {
            if (whatText.equals("In")) {
                if (isY) {
                    return 78;
                } else {
                    return 29;
                }
            } else if (whatText.equals("Out")) {
                if (isY) {
                    return 78;
                } else {
                    return 134;
                }
            } else if (whatText.equals("Inv")) {
                return 149;
            }
        } else if (getTier() == CableTier.CREATIVE) {
            if (whatText.equals("In")) {
                if (isY) {
                    return 95;
                } else {
                    return 35;
                }
            } else if (whatText.equals("Out")) {
                if (isY) {
                    return 95;
                } else {
                    return 128;
                }
            } else if (whatText.equals("Inv")) {
                return 163;
            }
        }
        return 0;
    }

    public class TieredIoModeSideButton extends SideButton {
        public TieredIoModeSideButton(BaseScreen<TieredDiskManipulatorContainer> screen) {
            super(screen);
        }

        @Override
        public String getTooltip() {
            return I18n.get("sidebutton.refinedstorage.iomode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.iomode." + (TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_INSERT ? "insert" : "extract"));
        }

        @Override
        protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
            screen.blit(poseStack, x, y, TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
        }

        @Override
        public void onPress() {
            BlockEntitySynchronizationManager.setParameter(TieredDiskManipulatorBlockEntity.IO_MODE, TieredDiskManipulatorBlockEntity.IO_MODE.getValue() == TieredDiskManipulatorNetworkNode.IO_MODE_INSERT ? TieredDiskManipulatorNetworkNode.IO_MODE_EXTRACT : TieredDiskManipulatorNetworkNode.IO_MODE_INSERT);
        }
    }
}
