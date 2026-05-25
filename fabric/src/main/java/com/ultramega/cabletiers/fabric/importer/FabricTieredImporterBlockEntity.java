package com.ultramega.cabletiers.fabric.importer;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.importer.AbstractTieredImporterBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FabricTieredImporterBlockEntity extends AbstractTieredImporterBlockEntity {
    public FabricTieredImporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return this.connections;
    }
}
