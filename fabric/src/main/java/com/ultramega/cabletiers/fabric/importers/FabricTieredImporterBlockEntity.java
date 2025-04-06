package com.ultramega.cabletiers.fabric.importers;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.importers.AbstractTieredImporterBlockEntity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricTieredImporterBlockEntity extends AbstractTieredImporterBlockEntity {
    public FabricTieredImporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return connections;
    }
}
