package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockModeSettings")
public interface InvokerLockModeSettings {
    @Invoker("getLockMode")
    static LockMode cabletiers$getLockMode(final int lockMode) {
        throw new AssertionError();
    }

    @Invoker("getLockMode")
    static int cabletiers$getLockMode(final LockMode lockMode) {
        throw new AssertionError();
    }
}
