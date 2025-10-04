package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.packet.c2s.RequestSidedResourcesPacket;
import com.ultramega.cabletiers.common.utils.ValidSlot;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.containermenu.ValidatedSlot;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ValidatedSlot.class)
public abstract class MixinValidatedSlot extends Slot implements ValidSlot {
    @Unique
    @Nullable
    private Container cabletiers$patternOutput;

    public MixinValidatedSlot(final Container container, final int slot, final int x, final int y) {
        super(container, slot, x, y);
    }

    @Override
    public void set(final ItemStack stack) {
        super.set(stack);

        if (cabletiers$patternOutput != null && container == cabletiers$patternOutput) {
            Platform.INSTANCE.sendPacketToServer(new RequestSidedResourcesPacket());
        }
    }

    @Override
    @Unique
    public void cabletiers$setPatternGridContainerMenu(final Container patternOutput) {
        this.cabletiers$patternOutput = patternOutput;
    }
}
