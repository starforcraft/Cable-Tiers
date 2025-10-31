package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LockMode.class)
public interface InvokerLockMode {
    @Invoker(value = "toggle", remap = false)
    LockMode cabletiers$toggle();
}
