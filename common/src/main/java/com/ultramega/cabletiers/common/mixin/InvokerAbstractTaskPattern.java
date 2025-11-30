package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.refinedmods.refinedstorage.api.autocrafting.task.AbstractTaskPattern")
public interface InvokerAbstractTaskPattern {
    @Accessor("pattern")
    Pattern cabletiers$getPattern();
}
