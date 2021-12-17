package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blockentity.TieredRequesterBlockEntity;
import com.YTrollman.CableTiers.container.TieredRequesterContainer;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.DetectorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class TieredRequesterScreen extends TieredScreen<TieredRequesterBlockEntity, TieredRequesterContainer, TieredRequesterNetworkNode> {

    private EditBox textField;

    public TieredRequesterScreen(TieredRequesterContainer container, Inventory inventory, Component title) {
        super(container, 211, 137 + (18 * (container.getTier().getSlotsMultiplier() - 1)), inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredRequesterBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredRequesterBlockEntity.TYPE));
        textField = new EditBox(Minecraft.getInstance().font, x + 86, y + 41 + (18 * (getTier().getSlotsMultiplier() - 1)), 80, 10, new TextComponent(""));
        textField.setValue(TieredRequesterBlockEntity.AMOUNT.getValue() + "");
        textField.setValue(String.valueOf(DetectorBlockEntity.AMOUNT.getValue()));
        //textField.setEnableBackgroundDrawing(false);
        textField.setVisible(true);
        textField.setCanLoseFocus(true);
        textField.setFocus(false);
        textField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        textField.setResponder(value -> {
            try {
                int result = Integer.parseInt(value);

                BlockEntitySynchronizationManager.setParameter(TieredRequesterBlockEntity.AMOUNT, result);
            } catch (NumberFormatException e) {
                // NO OP
            }
        });
        addWidget(textField);
    }

    public EditBox getTextField() {
        return textField;
    }

    @Override
    public void tick(int i, int i1) {
        textField.tick();
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_requester.png");
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
        if (TieredRequesterBlockEntity.MISSING.getValue()) {
            bindTexture(RS.ID, "gui/crafting_preview.png");
            blit(poseStack, x + 153, y + 1, 0, 256 - 16, 16, 16);
        }
        textField.render(poseStack, mouseX, mouseY, 0);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 43 + (18 * (getTier().getSlotsMultiplier() - 1)), new TranslatableComponent("container.inventory").getString());
        if (TieredRequesterBlockEntity.MISSING.getValue() && isHovering(153, 1, 16, 16, mouseX + leftPos, mouseY + topPos)) {
            renderTooltip(poseStack, new TranslatableComponent("tooltip.refinedstoragerequestify:requester.missing"), mouseX, mouseY);
        }
    }
}
