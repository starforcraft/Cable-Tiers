package com.ultramega.cabletiers.mixin;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CableCoverBakedModel.class)
public class MixinCableCoverBakedModel {
    @Inject(at = @At("TAIL"), method = "getHollowCoverSize", cancellable = true)
    private static void getHollowCoverSize(BlockState state, Direction coverSide, CallbackInfoReturnable<Integer> cir) {
        BaseBlock block = (BaseBlock) state.getBlock();

        if (block.getDirection() != null && state.getValue(block.getDirection().getProperty()) == coverSide) {
            for (CableTier tier : CableTier.VALUES) {
                if (block == ContentType.EXPORTER.getBlock(tier)) {
                    cir.setReturnValue(6);
                } else if (block == ContentType.IMPORTER.getBlock(tier)) {
                    cir.setReturnValue(3);
                } else if (block == ContentType.CONSTRUCTOR.getBlock(tier) || block == ContentType.DESTRUCTOR.getBlock(tier)) {
                    cir.setReturnValue(2);
                }
            }
        }
    }
}