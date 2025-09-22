package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PatternGridContainerMenu.class)
public interface MixinPatternGridContainerMenuInvoker {
    @Accessor(value = "patternGrid", remap = false)
    @Nullable
    PatternGridBlockEntity getPatternGrid();
}
