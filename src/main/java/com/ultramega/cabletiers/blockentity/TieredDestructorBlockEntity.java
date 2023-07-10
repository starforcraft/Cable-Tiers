package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.TieredDestructorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class TieredDestructorBlockEntity extends TieredBlockEntity<TieredDestructorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredDestructorBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_destructor_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredDestructorBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_destructor_whitelist_blacklist"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredDestructorBlockEntity> TYPE = IType.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_destructor_type"));
    public static final BlockEntitySynchronizationParameter<Boolean, TieredDestructorBlockEntity> PICKUP = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_destructor_pickup"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, TieredDestructorBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_destructor_cover_manager"), EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> {
            });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(COMPARE)
            .addWatchedParameter(WHITELIST_BLACKLIST)
            .addWatchedParameter(TYPE)
            .addWatchedParameter(PICKUP)
            .addWatchedParameter(COVER_MANAGER)
            .build();

    public TieredDestructorBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.DESTRUCTOR, tier, pos, state, SPEC);
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
