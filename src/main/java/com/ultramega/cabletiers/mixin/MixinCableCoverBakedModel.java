package com.ultramega.cabletiers.mixin;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value = CableCoverBakedModel.class)
public class MixinCableCoverBakedModel {

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static int getHollowCoverSize(@Nullable BlockState state, Direction coverSide) {
        if (state == null) {
            return 6;
        } else {
            BaseBlock block = (BaseBlock) state.getBlock();
            if (block == RSBlocks.CABLE.get()) {
                return 6;
            } else {
                if (block.getDirection() != null && state.getValue(block.getDirection().getProperty()) == coverSide) {
                    if (block == RSBlocks.EXPORTER.get()) {
                        return 6;
                    }

                    if (block == RSBlocks.EXTERNAL_STORAGE.get() || block == RSBlocks.IMPORTER.get()) {
                        return 3;
                    }

                    if (block == RSBlocks.CONSTRUCTOR.get() || block == RSBlocks.DESTRUCTOR.get()) {
                        return 2;
                    }

                    for (CableTier tier : CableTier.VALUES) {
                        if (block == ContentType.EXPORTER.getBlock(tier)) {
                            return 6;
                        }

                        if (block == ContentType.IMPORTER.getBlock(tier)) {
                            return 3;
                        }

                        if (block == ContentType.CONSTRUCTOR.getBlock(tier) || block == ContentType.DESTRUCTOR.getBlock(tier)) {
                            return 2;
                        }
                    }
                }

                return 6;
            }
        }
    }
}