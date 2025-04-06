package com.ultramega.cabletiers.fabric.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.constructordestructor.AbstractTieredConstructorBlockEntity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricTieredConstructorBlockEntity extends AbstractTieredConstructorBlockEntity {
    public FabricTieredConstructorBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return connections;
    }
}
