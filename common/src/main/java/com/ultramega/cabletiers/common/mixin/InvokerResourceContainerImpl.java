package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceContainerImpl.class)
public interface InvokerResourceContainerImpl {
    @Invoker(value = "changed", remap = false)
    void cabletiers$changed();
}
