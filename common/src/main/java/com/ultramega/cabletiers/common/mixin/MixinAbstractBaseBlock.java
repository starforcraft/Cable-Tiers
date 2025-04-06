package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.utils.TagFiltering;

import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBaseBlock.class)
public abstract class MixinAbstractBaseBlock {
    @Inject(method = "tryOpenScreen(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
        + "Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;)Ljava/util/Optional;", at = @At("RETURN"))
    private void tryOpenScreen(final BlockState state,
                               final Level level,
                               final BlockPos pos,
                               final Player player,
                               final Vec3 hit,
                               final CallbackInfoReturnable<Optional<InteractionResult>> cir) {
        // Send an update after the container menu was completely build
        if (level.getBlockEntity(pos) instanceof TagFiltering tagFiltering && player instanceof ServerPlayer serverPlayer) {
            tagFiltering.sendFilterTagsToClient(serverPlayer);
        }
    }
}
