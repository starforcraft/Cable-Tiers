package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.TieredRequesterContainer;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredRequesterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.tile.DetectorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TieredRequesterScreen extends TieredScreen<TieredRequesterTileEntity, TieredRequesterContainer, TieredRequesterNetworkNode> {

    private TextFieldWidget textField;

    public TieredRequesterScreen(TieredRequesterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 137 + (18 * (container.getTier().getSlotsMultiplier() - 1)), playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TieredRequesterTileEntity.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, TieredRequesterTileEntity.TYPE));
        textField = new TextFieldWidget(Minecraft.getInstance().font, x + 86, y + 41 + (18 * (getTier().getSlotsMultiplier() - 1)), 80, 10, new StringTextComponent(""));
        textField.setValue(TieredRequesterTileEntity.AMOUNT.getValue() + "");
        textField.setValue(String.valueOf(DetectorTile.AMOUNT.getValue()));
        //textField.setEnableBackgroundDrawing(false);
        textField.setVisible(true);
        textField.setCanLoseFocus(true);
        textField.setFocus(false);
        textField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        textField.setResponder(value -> {
            try {
                int result = Integer.parseInt(value);

                TileDataManager.setParameter(TieredRequesterTileEntity.AMOUNT, result);
            } catch (NumberFormatException e) {
                // NO OP
            }
        });
        addButton(textField);
    }

    public TextFieldWidget getTextField() {
        return textField;
    }

    @Override
    public void tick(int i, int i1) {
        textField.tick();
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(CableTiers.MOD_ID, "gui/" + getTier().getName() + "_requester.png");
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
        if (TieredRequesterTileEntity.MISSING.getValue()) {
            bindTexture(RS.ID, "gui/crafting_preview.png");
            blit(matrixStack, x + 153, y + 1, 0, 256 - 16, 16, 16);
        }
        textField.render(matrixStack, mouseX, mouseY, 0);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43 + (18 * (getTier().getSlotsMultiplier() - 1)), new TranslationTextComponent("container.inventory").getString());
        if (TieredRequesterTileEntity.MISSING.getValue() && isHovering(153, 1, 16, 16, mouseX + leftPos, mouseY + topPos)) {
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.refinedstoragerequestify:requester.missing"), mouseX, mouseY);
        }
    }
}
