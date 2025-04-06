package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.CableTiers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockEntityProvider<T extends BlockEntity> {
    T create(CableTiers tier, BlockPos pos, BlockState state);
}
