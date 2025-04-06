package com.ultramega.cabletiers.neoforge.exporters;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.exporters.AbstractTieredExporterBlockEntity;

import com.refinedmods.refinedstorage.neoforge.support.render.ModelProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ForgeTieredExporterBlockEntity extends AbstractTieredExporterBlockEntity {
    public ForgeTieredExporterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ModelProperties.CABLE_CONNECTIONS, connections).build();
    }
}
