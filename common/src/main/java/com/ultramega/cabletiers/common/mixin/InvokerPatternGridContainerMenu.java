package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PatternGridContainerMenu.class)
public interface InvokerPatternGridContainerMenu {
    @Accessor(value = "patternGrid", remap = false)
    @Nullable
    PatternGridBlockEntity cabletiers$getPatternGrid();

    @Invoker(value = "getPatternType", remap = false)
    PatternType cabletiers$getPatternType();
}
