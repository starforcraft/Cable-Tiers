package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.packet.c2s.RequestSidedResourcesPacket;
import com.ultramega.cabletiers.common.packet.s2c.SetSidedResourcesOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.utils.PlayerInventoryGetter;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu$3")
public class MixinInnerValidatedSlot {
    @Shadow(aliases = "this$0", remap = false)
    @Final
    private PatternGridContainerMenu patternGridContainerMenu;

    @Inject(method = "set", at = @At("TAIL"))
    private void set(final ItemStack stack, final CallbackInfo ci) {
        final Player player = ((PlayerInventoryGetter) patternGridContainerMenu).cabletiers$getPlayerInventory().player;

        if (player.level().isClientSide()) {
            Platform.INSTANCE.sendPacketToServer(new RequestSidedResourcesPacket());
        } else {
            if (player instanceof ServerPlayer serverPlayer
                && ((MixinPatternGridContainerMenuInvoker) patternGridContainerMenu).cabletiers$getPatternGrid() instanceof SidedInput sidedInput) {
                Platform.INSTANCE.sendPacketToClient(serverPlayer, new SetSidedResourcesOnPatternGridMenuPacket(sidedInput.cabletiers$getSidedResources()));
            }
        }
    }
}
