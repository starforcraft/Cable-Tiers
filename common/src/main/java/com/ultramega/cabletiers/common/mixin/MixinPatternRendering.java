package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterScreen;

import com.refinedmods.refinedstorage.common.autocrafting.PatternRendering;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternRendering.class)
public class MixinPatternRendering {
    @Inject(method = "canDisplayOutputInScreen", at = @At("HEAD"), cancellable = true)
    private static void canDisplayOutputInScreen(final ItemStack stack, final CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().screen instanceof TieredAutocrafterScreen autocrafterScreen) {
            if (autocrafterScreen.getMenu().containsPattern(stack)) {
                cir.setReturnValue(true);
            }
        }
    }
}
