package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

import java.util.Arrays;

public class TieredDiskManipulatorTileEntity extends TieredTileEntity<TieredDiskManipulatorNetworkNode> {
    public static final TileDataParameter<Integer, TieredDiskManipulatorTileEntity> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TieredDiskManipulatorTileEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TieredDiskManipulatorTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TieredDiskManipulatorTileEntity> IO_MODE = new TileDataParameter<>(DataSerializers.INT, TieredDiskManipulatorNetworkNode.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
        t.getNode().setIoMode(v);
        t.getNode().markDirty();
    });

    //public static final ModelProperty<DiskState[]> DISK_STATE_PROPERTY = new ModelProperty<>();

    //private static final String NBT_DISK_STATE = "DiskStates";

    //private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getDisks());

    private final DiskState[] diskState = new DiskState[6 * checkTierMultiplier()];

    public TieredDiskManipulatorTileEntity(CableTier tier) {
        super(ContentType.DISK_MANIPULATOR, tier);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

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
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        ListNBT list = new ListNBT();

        for (DiskState state : getNode().getDiskState()) {
            list.add(IntNBT.valueOf(state.ordinal()));
        }

        tag.put(NBT_DISK_STATE, list);

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
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
