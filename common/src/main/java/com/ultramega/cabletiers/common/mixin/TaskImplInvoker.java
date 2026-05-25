package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskImpl;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TaskImpl.class)
public interface TaskImplInvoker {
    @Accessor(value = "patterns", remap = false)
    Map<Pattern, AbstractTaskPatternAccessor> cabletiers$getPatterns();

    @Accessor(value = "completedPatterns", remap = false)
    List<AbstractTaskPatternAccessor> cabletiers$getCompletedPatterns();
}
