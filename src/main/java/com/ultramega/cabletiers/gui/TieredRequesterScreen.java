package com.ultramega.cabletiers.gui;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.DetectorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.blockentity.TieredRequesterBlockEntity;
import com.ultramega.cabletiers.container.TieredRequesterContainer;
import com.ultramega.cabletiers.node.TieredRequesterNetworkNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class TieredRequesterScreen extends TieredScreen<TieredRequesterBlockEntity, TieredRequesterContainer, TieredRequesterNetworkNode> {
    private EditBox textField;

    public TieredRequesterScreen(TieredRequesterContainer container, Inventory inventory, Component title) {
        super(container, 211, 137 + (18 * (container.getTier().getSlotsMultiplier() - 1)), inventory, title);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();
            return true;
        }
        if (textField.keyPressed(keyCode, scanCode, modifiers) || textField.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredRequesterBlockEntity.TYPE));
        textField = new EditBox(Minecraft.getInstance().font, x + 86, y + 41 + (18 * (getTier().getSlotsMultiplier() - 1)), 80, 10, Component.literal(""));
        textField.setValue(TieredRequesterBlockEntity.AMOUNT.getValue() + "");
        textField.setValue(String.valueOf(DetectorBlockEntity.AMOUNT.getValue()));
        //textField.setEnableBackgroundDrawing(false);
        textField.setVisible(true);
        textField.setCanLoseFocus(true);
        textField.setFocused(false);
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
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(new ResourceLocation(CableTiers.MOD_ID, "textures/gui/" + getTier().getName() + "_requester.png"), x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (TieredRequesterBlockEntity.MISSING.getValue()) {
            graphics.blit(new ResourceLocation(RS.ID, "textures/gui/crafting_preview.png"), x + 153, y + 1, 0, 256 - 16, 16, 16);
        }
        textField.render(graphics, mouseX, mouseY, 0);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 43 + (18 * (getTier().getSlotsMultiplier() - 1)), I18n.get("container.inventory"));
        if (TieredRequesterBlockEntity.MISSING.getValue() && isHovering(153, 1, 16, 16, mouseX + leftPos, mouseY + topPos)) {
            renderTooltip(graphics, mouseX, mouseY, Component.translatable("tooltip.refinedstoragerequestify:requester.missing").getString());
        }
    }
}
