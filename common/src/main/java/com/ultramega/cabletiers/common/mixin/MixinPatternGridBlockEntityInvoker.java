package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PatternGridBlockEntity.class)
public interface MixinPatternGridBlockEntityInvoker {
    @Invoker(value = "copyPattern", remap = false)
    void cabletiers$copyPattern(ItemStack stack);
}
