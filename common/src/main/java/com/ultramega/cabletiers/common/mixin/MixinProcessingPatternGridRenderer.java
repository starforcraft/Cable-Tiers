package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ultramega.cabletiers.common.utils.ClientUtils.renderDirectionText;
import static com.ultramega.cabletiers.common.utils.SidedInputUtil.isProcessingInputSlot;

@Mixin(targets = "com.refinedmods.refinedstorage.common.autocrafting.patterngrid.ProcessingPatternGridRenderer")
public final class MixinProcessingPatternGridRenderer {
    @Shadow(remap = false)
    @Final
    private PatternGridContainerMenu menu;
    @Shadow(remap = false)
    @Final
    private int topPos;
    @Shadow(remap = false)
    @Final
    private int leftPos;

    @Inject(method = "renderMatrixSlots", at = @At("TAIL"))
    private void renderMatrixSlots(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean input, final CallbackInfo ci) {
        if (!(menu instanceof SidedInput sidedInput)) {
            return;
        }

        final Font font = Minecraft.getInstance().font;

        int index = 0;
        for (final ResourceSlot resourceSlot : menu.getResourceSlots()) {
            if (resourceSlot.isActive() && isProcessingInputSlot(resourceSlot) && index < sidedInput.cabletiers$getSidedResources().size()) {
                final Optional<SidedResourceAmount> resource = sidedInput.cabletiers$getSidedResources().get(index);
                if (!resourceSlot.isEmpty() && resource.isPresent()) {
                    renderDirectionText(graphics, font, resource.get().inputDirection().orElse(null), resourceSlot.x + leftPos, resourceSlot.y + topPos);
                }

                index++;
            }
        }
    }
}
