package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;

import com.refinedmods.refinedstorage.common.storage.Disk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.NonNull;

public class ForgeTieredDiskInterfaceBlockEntity extends AbstractTieredDiskInterfaceBlockEntity {
    public static final ModelProperty<Disk[]> DISKS_PROPERTY = new ModelProperty<>();

    public ForgeTieredDiskInterfaceBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(tier, pos, state);
    }

    @NonNull
    @Override
    public ModelData getModelData() {
        if (this.disks == null) {
            return ModelData.EMPTY;
        }
        return ModelData.builder().with(DISKS_PROPERTY, this.disks).build();
    }
}
