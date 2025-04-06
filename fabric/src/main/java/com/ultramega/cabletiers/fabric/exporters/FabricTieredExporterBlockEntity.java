package com.ultramega.cabletiers.fabric.exporters;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.exporters.AbstractTieredExporterBlockEntity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricTieredExporterBlockEntity extends AbstractTieredExporterBlockEntity {
    public FabricTieredExporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return connections;
    }
}
