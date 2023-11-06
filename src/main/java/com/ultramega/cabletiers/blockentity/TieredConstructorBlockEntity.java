package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.TieredConstructorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class TieredConstructorBlockEntity extends TieredBlockEntity<TieredConstructorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredConstructorBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_constructor_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredConstructorBlockEntity> TYPE = IType.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_constructor_type"));
    public static final BlockEntitySynchronizationParameter<Boolean, TieredConstructorBlockEntity> DROP = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_constructor_drop"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, TieredConstructorBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_constructor_cover_manager"), EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> {
            });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(COMPARE)
            .addWatchedParameter(TYPE)
            .addWatchedParameter(DROP)
            .addWatchedParameter(COVER_MANAGER)
            .build();

    public TieredConstructorBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.CONSTRUCTOR, tier, pos, state, SPEC);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));

        requestModelDataUpdate();

        LevelUtils.updateBlock(level, worldPosition);
    }
}
