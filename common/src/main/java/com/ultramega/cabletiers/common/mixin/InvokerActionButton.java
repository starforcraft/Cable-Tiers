package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.support.amount.ActionButton;

import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActionButton.class)
public interface InvokerActionButton {
    @Invoker("<init>")
    static ActionButton init(final int x,
                             final int y,
                             final int width,
                             final int height,
                             final Component message,
                             final OnPress onPress) {
        throw new AssertionError();
    }
}
