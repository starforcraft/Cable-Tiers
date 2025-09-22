package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ultramega.cabletiers.common.utils.ClientUtils.renderDirectionText;

@Mixin(targets = "com.refinedmods.refinedstorage.common.autocrafting.ProcessingPatternClientTooltipComponent")
public abstract class MixinProcessingPatternClientTooltipComponent implements SidedInput {
    @Shadow(remap = false)
    @Final
    private List<List<ResourceAmount>> inputs;
    @Shadow(remap = false)
    @Final
    private int rows;

    @Unique
    private List<Optional<SidedResourceAmount>> cabletiers$sidedResources = new ArrayList<>();

    @Inject(method = "renderMatrixSlots", at = @At("HEAD"))
    private void renderMatrixSlots(final int x, final int y, final boolean input, final GuiGraphics graphics, final CallbackInfo ci) {
        if (cabletiers$sidedResources.isEmpty() || !input) {
            return;
        }

        final int maxSize = inputs.size();
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < 3; ++column) {
                final int slotX = x + column * 18;
                final int slotY = y + row * 18;
                final int idx = row * 3 + column;
                if (idx >= maxSize) {
                    break;
                }

                final Optional<SidedResourceAmount> resource = cabletiers$sidedResources.get(idx);
                if (resource.isPresent()) {
                    final Font font = Minecraft.getInstance().font;
                    renderDirectionText(graphics, font, resource.get().inputDirection().orElse(null), slotX + 1, slotY + 1);
                }
            }
        }
    }

    @Unique
    @Override
    public void cabletiers$setSidedResources(final List<Optional<SidedResourceAmount>> sidedResources) {
        this.cabletiers$sidedResources = sidedResources;
    }

    @Unique
    @Override
    public List<Optional<SidedResourceAmount>> cabletiers$getSidedResources() {
        return cabletiers$sidedResources;
    }
}
