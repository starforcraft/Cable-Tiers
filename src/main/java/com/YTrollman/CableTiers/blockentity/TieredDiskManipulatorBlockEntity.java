package com.YTrollman.CableTiers.blockentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;

public class TieredDiskManipulatorBlockEntity extends TieredBlockEntity<TieredDiskManipulatorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> TYPE = IType.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, TieredDiskManipulatorBlockEntity> IO_MODE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, DiskManipulatorNetworkNode.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
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
        if (getTier() == CableTier.ELITE) {
            return 2;
        } else if (getTier() == CableTier.ULTRA) {
            return 3;
        } else if (getTier() == CableTier.CREATIVE) {
            return 4;
        }
        return 0;
    }

    /*@Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        ListNBT list = new ListNBT();

        for (DiskState state : getNode().getDiskState()) {
            list.add(IntNBT.valueOf(state.ordinal()));
        }

        tag.put(NBT_DISK_STATE, list);

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        ListNBT list = tag.getList(NBT_DISK_STATE, Constants.NBT.TAG_INT);

        for (int i = 0; i < list.size(); ++i) {
            diskState[i] = DiskState.values()[list.getInt(i)];
        }

        requestModelDataUpdate();

        WorldUtils.updateBlock(world, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(DISK_STATE_PROPERTY, diskState).build();
    }*/
}
