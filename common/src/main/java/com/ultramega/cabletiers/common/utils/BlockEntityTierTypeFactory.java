package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.CableTiers;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@FunctionalInterface
public interface BlockEntityTierTypeFactory {
    <T extends BlockEntity> BlockEntityType<T> create(CableTiers tier, BlockEntityTierProvider<T> factory, Block... allowedBlocks);
}
