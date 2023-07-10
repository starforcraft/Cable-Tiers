package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.diskmanipulator.TieredDiskManipulatorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TieredDiskManipulatorBlockEntity extends TieredBlockEntity<TieredDiskManipulatorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_disk_manipulator_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_disk_manipulator_whitelist_blacklist"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> TYPE = IType.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_disk_manipulator_type"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> IO_MODE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_disk_manipulator_mode"), EntityDataSerializers.INT, DiskManipulatorNetworkNode.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
        t.getNode().setIoMode(v);
        t.getNode().markDirty();
    });

    public static final ModelProperty<DiskState[]> DISK_STATE_PROPERTY = new ModelProperty<>();

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(COMPARE)
            .addWatchedParameter(WHITELIST_BLACKLIST)
            .addWatchedParameter(TYPE)
            .addWatchedParameter(IO_MODE)
            .build();

    private static final String NBT_DISK_STATE = "DiskStates";

    private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getDisks());

    private final DiskState[] diskState = new DiskState[6 * checkTierMultiplier()];

    public TieredDiskManipulatorBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.DISK_MANIPULATOR, tier, pos, state, SPEC);
        Arrays.fill(diskState, DiskState.NONE);
    }

    private int checkTierMultiplier() {
        return switch (getTier()) {
            case ELITE -> 2;
            case ULTRA -> 3;
            case MEGA -> 4;
        };
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        ListTag list = new ListTag();

        for (DiskState state : getNode().getDiskState()) {
            list.add(IntTag.valueOf(state.ordinal()));
        }

        tag.put(NBT_DISK_STATE, list);

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        ListTag list = tag.getList(NBT_DISK_STATE, Tag.TAG_INT);

        for (int i = 0; i < list.size(); ++i) {
            diskState[i] = DiskState.values()[list.getInt(i)];
        }

        requestModelDataUpdate();

        LevelUtils.updateBlock(level, worldPosition);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(DISK_STATE_PROPERTY, diskState).build();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return diskCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
